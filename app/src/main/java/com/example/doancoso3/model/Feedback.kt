package com.example.doancoso3.model

data class Feedback(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val productId: String = "",
    val productName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String = "",
    val timestamp: Long = 0
)