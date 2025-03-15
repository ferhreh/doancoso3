package com.example.doancoso3.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.content.Context


class UserDb(private val context: Context, private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_USERS = "User"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_USERNAME = "name"
    }

    fun getUserId(email: String, password: String): Int {
        val cursor = db.rawQuery("SELECT id FROM User WHERE Email = ? AND Password = ?", arrayOf(email, password))

        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            -1
        }

        cursor.close()
        return userId
    }

    fun checkUser(email: String, password: String): Boolean {
        val cursor = db.rawQuery("SELECT * FROM User WHERE email = ? AND password = ?", arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    // Phương thức để thêm người dùng mới vào cơ sở dữ liệu
    fun addUser(name: String, email: String, password: String) {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        db.insert(TABLE_USERS, null, values)
    }
}