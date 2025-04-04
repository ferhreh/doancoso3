package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val dbHelper: CopyDbHelper,private val userId: Int) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    init {
        loadFavorites() // Gọi hàm này khi ViewModel được khởi tạo
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = dbHelper.getFavorites(userId)
        }
    }

    fun addToFavorites(product: Product, userId:Int) {
        viewModelScope.launch {
            dbHelper.addFavorite(product, userId)
            loadFavorites() // Cập nhật danh sách yêu thích sau khi thêm
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            dbHelper.removeFavorite(product, userId)
            loadFavorites() // Cập nhật danh sách yêu thích sau khi xóa
        }
    }
}

