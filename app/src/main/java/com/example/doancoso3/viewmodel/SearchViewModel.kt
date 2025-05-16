package com.example.doancoso3.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel(
    private val userId: String
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _bestsellers = MutableStateFlow<List<Product>>(emptyList())
    val bestsellers: StateFlow<List<Product>> = _bestsellers

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    private val _suggestedKeyword = MutableStateFlow<String?>(null)
    val suggestedKeyword: StateFlow<String?> = _suggestedKeyword

    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                // normalizedQuery loại bỏ khoảng trắng đầu và cuối chuỗi
                val normalizedQuery = query.trim()
                // kiểm tra chỗi có ký tự đặt biệt không
                val onlySpecialChars = normalizedQuery.all { !it.isLetterOrDigit() }

                val result = db.collection("products").get().await()
                val products = result.documents.mapNotNull { it.toObject(Product::class.java) }

                val filtered = if (onlySpecialChars) {
                    emptyList()
                } else {
                    products.filter { product ->
                        product.TenSP.contains(normalizedQuery, ignoreCase = true) ||
                                product.MoTa.contains(normalizedQuery, ignoreCase = true) ||
                                product.DanhMuc.contains(normalizedQuery, ignoreCase = true)
                    }
                }

                _searchResults.value = filtered

                if (filtered.isEmpty() && !onlySpecialChars) {
                    val suggestions = products.map { it.TenSP }
                        .filterNot { it.equals(normalizedQuery, ignoreCase = true) }
                        .distinct()
                        .filter { it.lowercase().contains(normalizedQuery.take(2).lowercase()) }

                    _suggestedKeyword.value = suggestions.firstOrNull()
                } else {
                    _suggestedKeyword.value = null
                }

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error searching products", e)
            }
        }
    }

    suspend fun loadBestsellers() {
        try {
            // Kiểm tra xem có orders trong Firestore không
            val usersSnapshot = db.collection("orders")
                .get()
                .await()

            Log.d("SearchViewModel", "Total Users: ${usersSnapshot.size()}")

            if (usersSnapshot.isEmpty) {
                Log.d("SearchViewModel", "No users found in Firestore.")
            }

            val productCounts = mutableMapOf<String, Int>()

            // Lặp qua từng userId
            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                Log.d("SearchViewModel", "Processing Orders for User: $userId")

                // Kiểm tra xem có order_items trong userId hay không
                val itemsSnapshot = db.collection("orders")
                    .document(userId)
                    .collection("order_items")
                    .get()
                    .await()

                Log.d("SearchViewModel", "Order Items Count for $userId: ${itemsSnapshot.size()}")

                if (itemsSnapshot.isEmpty) {
                    Log.d("SearchViewModel", "No order items found for user $userId.")
                }

                // Đếm số lượng sản phẩm bán được
                for (itemDoc in itemsSnapshot.documents) {
                    val productId = itemDoc.id
                    val quantity = itemDoc.getLong("soLuong")?.toInt() ?: 0

                    // Tăng số lượng sản phẩm đã bán
                    productCounts[productId] = productCounts.getOrDefault(productId, 0) + quantity
                }
            }

            if (productCounts.isEmpty()) {
                Log.d("SearchViewModel", "No products sold found in Firestore.")
            } else {
                Log.d("SearchViewModel", "Product Counts: $productCounts")
            }

            // Sắp xếp và lấy Top 10
            val sortedProductIds = productCounts.entries
                .sortedByDescending { it.value }
                .map { it.key }
                .take(10)

            Log.d("SearchViewModel", "Sorted Product IDs (Top 10): $sortedProductIds")

            // Lấy thông tin chi tiết của sản phẩm
            val products = sortedProductIds.mapNotNull { id ->
                val productSnapshot = db.collection("products").document(id).get().await()
                Log.d("SearchViewModel", "Product Data for ID $id: $productSnapshot")
                productSnapshot.toObject(Product::class.java)
            }

            _bestsellers.value = products

            Log.d("SearchViewModel", "Best Sellers: $products")

        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error loading bestsellers", e)
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("favorites")
                    .whereEqualTo("userID", userId)
                    .get()
                    .await()

                val favoriteProducts = snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
                _favorites.value = favoriteProducts
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Failed to load favorites", e)
            }
        }
    }

    class SearchViewModelFactory(
        private val userId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(userId) as T
        }
    }
}
