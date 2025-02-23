package com.example.doancoso3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.model.Product


class CartViewModel : ViewModel() {
    // Dùng mutableStateListOf để cập nhật UI ngay lập tức
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(product: Product, quantity: Int) {
        val existingItem = _cartItems.find { it.product.ID == product.ID }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            _cartItems.add(CartItem(product, quantity))
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItems.remove(cartItem)
    }

    fun increaseQuantity(cartItem: CartItem) {
        val index = _cartItems.indexOfFirst { it.product.ID == cartItem.product.ID }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        }
    }

    fun decreaseQuantity(cartItem: CartItem) {
        val index = _cartItems.indexOfFirst { it.product.ID == cartItem.product.ID }
        if (index != -1 && _cartItems[index].quantity > 1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
        }
    }
}

