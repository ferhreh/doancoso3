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
    val totalAmount: Double = 0.0,
    val status: Int = 1,
    val productName: String,  // Add this to store product name
    val productPrice: Double, // Add this to store product price
)