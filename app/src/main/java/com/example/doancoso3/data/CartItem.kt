package com.example.doancoso3.data

import com.example.doancoso3.model.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)