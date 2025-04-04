package com.example.doancoso3.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val context: Context,
    private val userId: Int
) : ViewModel() {
    private val dbHelper = CopyDbHelper(context)

    // StateFlows cho Jetpack Compose
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _bestsellers = MutableStateFlow<List<Product>>(emptyList())
    val bestsellers: StateFlow<List<Product>> = _bestsellers

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    // ID người dùng hiện tại (trong thực tế, bạn sẽ lấy từ đâu đó)
    private val currentUserId = 1

    // Tìm kiếm sản phẩm
    suspend fun searchProducts(query: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val productDb = dbHelper.getProductDb()
                val allProducts = productDb.getProducts()

                // Lọc sản phẩm theo từ khóa tìm kiếm
                val filteredProducts = allProducts.filter {
                    it.TenSP.contains(query, ignoreCase = true) ||
                            it.MoTa.contains(query, ignoreCase = true) ||
                            it.DanhMuc.contains(query, ignoreCase = true)
                }

                _searchResults.value = filteredProducts
            }
        }
    }

    // Lấy danh sách sản phẩm bán chạy (từ bảng Orders)
    fun loadBestsellers() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val orderDb = dbHelper.getOrderDb()
                val orders = orderDb.getOrdersByUserId(userId)
                // Nhóm đơn hàng theo sản phẩm và đếm số lượng
                val productCounts = orders.groupBy { it.productId }
                    .mapValues { (_, orders) -> orders.sumOf { it.soLuong } }

                // Sắp xếp theo số lượng đã bán và lấy top sản phẩm
                val topProductIds = productCounts.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { it.key }

                // Lấy thông tin chi tiết của sản phẩm
                val topProducts = topProductIds.mapNotNull { productId ->
                    dbHelper.getProductById(productId)
                }

                _bestsellers.value = topProducts
            }
        }
    }

    // Lấy danh sách sản phẩm yêu thích
    fun loadFavorites() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val favoriteProducts = dbHelper.getFavorites(userId)
                _favorites.value = favoriteProducts
            }
        }
    }
}

class SearchViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(context, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}