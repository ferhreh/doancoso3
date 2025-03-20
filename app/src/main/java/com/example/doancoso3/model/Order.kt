package com.example.doancoso3.model

data class Order(
    val id: Int = 0,
    val userID: Int,
    val userName: String,
    val productId: Int,
    val address: String,
    val phoneNumber: String,
    val soLuong: Int,
    val paymentMethod: String,
    val deliveryMethod: String,
    val orderDate: String = "",
    val totalAmount: Double = 0.0
)