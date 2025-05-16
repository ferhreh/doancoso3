package com.example.doancoso3.util

import com.example.doancoso3.viewmodel.NotificationViewModel

object NotificationViewModelProvider {
    private val instance = NotificationViewModel()

    fun getInstance(): NotificationViewModel {
        return instance
    }
}