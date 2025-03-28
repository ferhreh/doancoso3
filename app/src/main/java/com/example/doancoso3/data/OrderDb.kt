package com.example.doancoso3.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.doancoso3.model.Order

class OrderDb(private val db: SQLiteDatabase) {
    companion object {
        const val TABLE_ORDERS = "Orders"
    }

    fun addOrder(order: Order): Long {
        val values = ContentValues().apply {
            put("UserID", order.userID)
            put("UserName", order.userName)
            put("ProductId", order.productId)
            put("Address", order.address)
            put("PhoneNumber", order.phoneNumber)
            put("SoLuong", order.soLuong)
            put("PaymentMethod", order.paymentMethod)
            put("DeliveryMethod", order.deliveryMethod)
            put("OrderDate", order.orderDate)
            put("TotalAmount", order.totalAmount)
            put("Status", order.status)
            put("ProductName", order.productName)
            put("ProductPrice", order.productPrice)
        }
        return db.insert(TABLE_ORDERS, null, values)
    }

    fun getOrdersByUserId(userId: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val cursor = db.query(
            TABLE_ORDERS,
            null,
            "UserID = ?",
            arrayOf(userId.toString()),
            null, null, "OrderDate DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val order = Order(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow("UserName")),
                    productId = cursor.getInt(cursor.getColumnIndexOrThrow("ProductId")),
                    address = cursor.getString(cursor.getColumnIndexOrThrow("Address")),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber")),
                    soLuong = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong")),
                    paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("PaymentMethod")),
                    deliveryMethod = cursor.getString(cursor.getColumnIndexOrThrow("DeliveryMethod")),
                    orderDate = cursor.getString(cursor.getColumnIndexOrThrow("OrderDate")),
                    totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalAmount")),
                    status = cursor.getInt(cursor.getColumnIndexOrThrow("Status")),
                    productName = cursor.getString(cursor.getColumnIndexOrThrow("ProductName")),
                    productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("ProductPrice")),
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return orders
    }
    // Thêm hàm cập nhật trạng thái đơn hàng
    fun updateOrderStatus(orderId: Int, status: Int): Boolean {
        val values = ContentValues().apply {
            put("Status", status)
        }

        val rowsAffected = db.update(
            TABLE_ORDERS,
            values,
            "id = ?",
            arrayOf(orderId.toString())
        )

        return rowsAffected > 0
    }
}
