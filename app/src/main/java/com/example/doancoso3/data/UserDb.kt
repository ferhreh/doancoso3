package com.example.doancoso3.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.doancoso3.model.User

class UserDb(private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_USERS = "User"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_USERNAME = "name"
    }

    // Phương thức để lấy danh sách người dùng từ cơ sở dữ liệu
    fun getUsers(): List<User> {
        val users = mutableListOf<User>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        cursor.use {
            while (it.moveToNext()) {
                val emailIndex = it.getColumnIndex(COLUMN_EMAIL)
                val passwordIndex = it.getColumnIndex(COLUMN_PASSWORD)
                val usernameIndex = it.getColumnIndex(COLUMN_USERNAME)

                val email = it.getString(emailIndex)
                val password = it.getString(passwordIndex)
                val username = it.getString(usernameIndex)

                // Thêm đối tượng User vào danh sách
                users.add(User(username, email, password))
            }
        }
        return users
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