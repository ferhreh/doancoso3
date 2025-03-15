package com.example.doancoso3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doancoso3.data.CopyDbHelper

class FavoritesViewModelFactory(private val dbHelper: CopyDbHelper,private val userId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(dbHelper,userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
