package com.example.doancoso3.data

import com.example.doancoso3.model.Product
data class CartItem(
    val userId: String = "",
    val product: Product,
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val productPrice: Double = 0.0,
    var quantity: Int=1 ,
)