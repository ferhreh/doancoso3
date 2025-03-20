package com.example.doancoso3.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.doancoso3.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        }
        return db.insert(TABLE_ORDERS, null, values)
    }

    fun getOrderById(orderId: Int): Order? {
        val cursor: Cursor = db.query(
            TABLE_ORDERS,
            null,
            "id = ?",
            arrayOf(orderId.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
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
                totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalAmount"))
            )
            cursor.close()
            order
        } else {
            cursor.close()
            null
        }
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
                    totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalAmount"))
                )
                orders.add(order)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return orders
    }
}
