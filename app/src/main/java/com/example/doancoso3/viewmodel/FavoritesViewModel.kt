package com.example.doancoso3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.FavoritesRepository
import com.example.doancoso3.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val userId: String) : ViewModel() {
    private val repository = FavoritesRepository()
    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites


    init {
        if (userId.isNotBlank()) {
            loadFavorites()
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val favs = repository.getFavorites(userId)
            _favorites.value = favs
        }
    }

    fun addToFavorites(product: Product, userId: String) {
        viewModelScope.launch {
            val productWithUser = product.copy(userId = userId)
            repository.addFavorite(productWithUser, userId)
            loadFavorites()
        }
    }

    fun removeFromFavorites(userId: String, product: Product) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(userId, product.ID)
                loadFavorites() // 👉 Load lại sau khi xóa
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error removing favorite", e)
            }
        }
    }
}
