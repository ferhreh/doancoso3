package com.example.doancoso3.data

import android.util.Log
import com.example.doancoso3.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
        private val ordersCollection = db.collection("orders")

    suspend fun addOrder(userId: String, order: Order): Boolean {
        return try {
            // 🔧 Tạo document cha nếu chưa tồn tại
            ordersCollection
                .document(userId)
                .set(hashMapOf("createdAt" to System.currentTimeMillis()))
                .await()

            val orderMap = hashMapOf(
                "userID" to order.userID,
                "userName" to order.userName,
                "productId" to order.productId,
                "address" to order.address,
                "phoneNumber" to order.phoneNumber,
                "soLuong" to order.soLuong,
                "paymentMethod" to order.paymentMethod,
                "deliveryMethod" to order.deliveryMethod,
                "orderDate" to order.orderDate,
                "totalAmount" to order.totalAmount,
                "status" to order.status,
                "productName" to order.productName,
                "productPrice" to order.productPrice,
                "isReviewed" to false
            )

            ordersCollection
                .document(userId)
                .collection("order_items")
                .add(orderMap)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateOrderStatus(userId: String, orderId: String, status: Int): Boolean {
        return try {
            ordersCollection
                .document(userId)
                .collection("order_items")
                .document(orderId)
                .update("status", status)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun markOrderAsReviewed(userId: String, orderId: String): Boolean {
        return try {
            ordersCollection
                .document(userId)
                .collection("order_items")
                .document(orderId)
                .update("isReviewed", true)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getOrderById(userId: String, orderId: String): Order? {
        return try {
            val doc = ordersCollection
                .document(userId)
                .collection("order_items")
                .document(orderId)
                .get()
                .await()

            if (doc.exists()) {
                val isReviewed = doc.getBoolean("isReviewed") ?: false
                Order(
                    id = doc.id,
                    userID = userId,
                    userName = doc["userName"] as String,
                    productId = doc["productId"] as String,
                    address = doc["address"] as String,
                    phoneNumber = doc["phoneNumber"] as String,
                    soLuong = (doc["soLuong"] as Long).toInt(),
                    paymentMethod = doc["paymentMethod"] as String,
                    deliveryMethod = doc["deliveryMethod"] as String,
                    orderDate = doc["orderDate"] as String,
                    totalAmount = doc["totalAmount"] as Double,
                    status = (doc["status"] as Long).toInt(),
                    productName = doc["productName"] as String,
                    productPrice = (doc["productPrice"] as Number).toLong(),
                    isReviewed = isReviewed
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getOrdersByUserId(userId: String): List<Order> {
        return try {
            val snapshot = ordersCollection
                .document(userId)
                .collection("order_items")
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    // ✅ Kiểm tra và cập nhật nếu thiếu trường isReviewed
                    val isReviewed = doc.getBoolean("isReviewed") ?: false
                    if (!doc.contains("isReviewed")) {
                        doc.reference.update("isReviewed", false).await()
                    }

                    Order(
                        id = doc.id,
                        userID = userId,
                        userName = doc["userName"] as String,
                        productId = doc["productId"] as String,
                        address = doc["address"] as String,
                        phoneNumber = doc["phoneNumber"] as String,
                        soLuong = (doc["soLuong"] as Long).toInt(),
                        paymentMethod = doc["paymentMethod"] as String,
                        deliveryMethod = doc["deliveryMethod"] as String,
                        orderDate = doc["orderDate"] as String,
                        totalAmount = doc["totalAmount"] as Double,
                        status = (doc["status"] as Long).toInt(),
                        productName = doc["productName"] as String,
                        productPrice = (doc["productPrice"] as Number).toLong(),
                        isReviewed = isReviewed // ✅ Sử dụng trường mới
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllOrders(): List<Order> {
        val allOrders = mutableListOf<Order>()
        try {
            val usersSnapshot = db.collection("orders").get().await()

            for (orderDoc in usersSnapshot.documents) {
                val orderDocId = orderDoc.id
                val userOrders = getOrdersByUserId(orderDocId)
                allOrders.addAll(userOrders)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Lỗi khi lấy tất cả đơn hàng", e)
        }
        return allOrders
    }
}
