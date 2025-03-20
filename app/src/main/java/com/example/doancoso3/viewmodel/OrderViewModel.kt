package com.example.doancoso3.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.data.OrderDb
import com.example.doancoso3.model.Order
import com.example.doancoso3.model.UserAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel(private val context: Context) : ViewModel() {
    private val orderDb: OrderDb by lazy {
        CopyDbHelper(context).getOrderDb()
    }

    val orderPlaced = mutableStateOf(false)

    fun placeOrder(
        userId: Int,
        userName: String,
        cartItems: List<CartItem>,
        address: UserAddress,
        paymentMethod: String,
        deliveryMethod: String
    ): Boolean {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        try {
            for (item in cartItems) {
                val order = Order(
                    userID = userId,
                    userName = userName,
                    productId = item.product.ID,
                    address = address.address,
                    phoneNumber = address.phoneNumber,
                    soLuong = item.quantity,
                    paymentMethod = paymentMethod,
                    deliveryMethod = deliveryMethod,
                    orderDate = currentDate,
                    totalAmount = item.product.GiaTien * item.quantity
                )

                orderDb.addOrder(order)
            }

            orderPlaced.value = true
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun resetOrderState() {
        orderPlaced.value = false
    }
}