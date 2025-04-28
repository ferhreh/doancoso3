package com.example.doancoso3.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.data.OrderFirestoreRepository
import com.example.doancoso3.model.Order
import com.example.doancoso3.model.UserAddress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel : ViewModel() {

    private val orderRepo = OrderFirestoreRepository()

    val orderPlaced = mutableStateOf(false)
    private val _autoDeliverableOrders = mutableStateOf<List<String>>(emptyList())
    val autoDeliverableOrders: List<String> get() = _autoDeliverableOrders.value

    fun placeOrder(
        userId: String,
        userName: String,
        cartItems: List<CartItem>,
        address: UserAddress,
        paymentMethod: String,
        deliveryMethod: String,
        onSuccess: () -> Unit
    ) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val shippingFee = 30000.0

        viewModelScope.launch {
            val orderIds = mutableListOf<String>()
            var allSuccess = true

            for (item in cartItems) {
                val order = Order(
                    userID = userId,
                    userName = userName,
                    productId = item.product.ID,
                    address = address.address,
                    phoneNumber = address.phone,
                    soLuong = item.quantity,
                    paymentMethod = paymentMethod,
                    deliveryMethod = deliveryMethod,
                    orderDate = currentDate,
                    totalAmount = (item.product.GiaTien * item.quantity) + shippingFee,
                    status = 1,
                    productName = item.product.TenSP,
                    productPrice = item.product.GiaTien,
                    id = ""
                )

                val success = orderRepo.addOrder(userId, order)
                if (success) {
                    orderIds.add(item.product.ID)
                } else {
                    allSuccess = false
                }
            }

            if (allSuccess) {
                orderPlaced.value = true
                onSuccess() // ✅ Gọi ngay sau khi đặt hàng thành công

                // Tiếp tục delay để xử lý đơn hàng tự động (sau 2 phút)
                delay(20000)
                _autoDeliverableOrders.value = orderIds
            } else {
                // TODO: xử lý khi có lỗi đặt hàng
            }
        }
    }


    fun isOrderAutoDeliverable(order: Order): Boolean {
        return autoDeliverableOrders.isNotEmpty()
    }

    fun getOrdersByUserId(userId: String, onResult: (List<Order>) -> Unit) {
        viewModelScope.launch {
            val orders = orderRepo.getOrdersByUserId(userId)
            onResult(orders)
        }
    }

    fun updateOrderStatus(userId: String, documentId: String, status: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = orderRepo.updateOrderStatus(userId, documentId, status)
            onResult(success)
        }
    }

    fun resetOrderState() {
        orderPlaced.value = false
    }
}
