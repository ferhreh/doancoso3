package com.example.doancoso3.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.data.OrderDb
import com.example.doancoso3.data.ProductDb
import com.example.doancoso3.model.Order
import kotlinx.coroutines.delay
import com.example.doancoso3.model.UserAddress
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel(private val context: Context) : ViewModel() {
    private val orderDb: OrderDb by lazy {
        CopyDbHelper(context).getOrderDb()
    }
    private val productDb: ProductDb by lazy {
        CopyDbHelper(context).getProductDb()
    }
    val orderPlaced = mutableStateOf(false)
    private val _autoDeliverableOrders = mutableStateOf<List<Int>>(emptyList())
    val autoDeliverableOrders: List<Int> get() = _autoDeliverableOrders.value
    fun placeOrder(
        userId: Int,
        userName: String,
        cartItems: List<CartItem>,
        address: UserAddress,
        paymentMethod: String,
        deliveryMethod: String
    ): Boolean {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val shippingFee = 30000.0

        try {
            var orderCount = 0
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
                    totalAmount = (item.product.GiaTien * item.quantity) + shippingFee,
                    status = 1 ,
                    productName = item.product.TenSP,
                    productPrice = item.product.GiaTien
                )

                orderDb.addOrder(order)
                orderCount++
            }
            viewModelScope.launch {
                delay(120000) // 1 minute delay
                _autoDeliverableOrders.value = listOf(orderCount)
            }
            orderPlaced.value = true
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    fun isOrderAutoDeliverable(order: Order): Boolean {
        return autoDeliverableOrders.isNotEmpty()
    }
    // Lấy danh sách đơn hàng của người dùng
    fun getOrdersByUserId(userId: Int): List<Order> {
        return orderDb.getOrdersByUserId(userId)
    }

    // Cập nhật trạng thái đơn hàng
    fun updateOrderStatus(orderId: Int, status: Int): Boolean {
        return orderDb.updateOrderStatus(orderId, status)
    }
    fun resetOrderState() {
        orderPlaced.value = false
    }

}