package com.example.doancoso3.data

import com.example.doancoso3.model.Product

data class CartItem(
    val userId: Int,
    val product: Product,
    var quantity: Int
)