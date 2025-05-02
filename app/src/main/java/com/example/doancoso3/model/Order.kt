package com.example.doancoso3.model

data class Order(
    val id: String = "",
    val userID: String = "",
    val userName: String,
    val productId: String = "",
    val address: String,
    val phoneNumber: String,
    val soLuong: Int,
    val paymentMethod: String,
    val deliveryMethod: String,
    val orderDate: String = "",
    val totalAmount: Double = 0.0,
    val status: Int = 1,
    val productName: String,
    val productPrice: Long = 0L,
    var isReviewed: Boolean = false
)