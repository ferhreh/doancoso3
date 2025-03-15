package com.example.doancoso3.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.doancoso3.model.UserAddress

class UserAddressDb(private val db: SQLiteDatabase) {

    fun addUserAddress(userId: Int, name: String, address: String, phoneNumber: String): Long {
        val values = ContentValues().apply {
            put("UserId", userId)
            put("Name", name)
            put("Address", address)
            put("PhoneNumber", phoneNumber)
        }
        return db.insert("UserAddress", null, values)
    }
    fun updateUserAddress(id: Int, name: String, address: String, phoneNumber: String): Int {
        val values = ContentValues().apply {
            put("Name", name)
            put("Address", address)
            put("PhoneNumber", phoneNumber)
        }
        return db.update("UserAddress", values, "id=?", arrayOf(id.toString()))
    }

    fun getUserAddresses(userId: Int): List<UserAddress> {
        val list = mutableListOf<UserAddress>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM UserAddress WHERE UserId = ?", arrayOf(userId.toString()))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
            val address = cursor.getString(cursor.getColumnIndexOrThrow("Address"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber"))
            list.add(UserAddress(id, userId, name, address, phone))
        }
        cursor.close()
        return list
    }

    fun deleteUserAddress(id: Int): Int {
        return db.delete("UserAddress", "id=?", arrayOf(id.toString()))
    }
}
