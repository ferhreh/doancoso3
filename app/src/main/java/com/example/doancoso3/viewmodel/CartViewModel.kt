package com.example.doancoso3.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product


class CartViewModel(private val dbHelper: CopyDbHelper) : ViewModel() {
    // Dùng mutableStateListOf để cập nhật UI ngay lập tức
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems
    private val TABLE_CART = CopyDbHelper.TABLE_CART
    fun clearCart(userId: Int) {
        val db = dbHelper.openDatabase()

        // Kiểm tra xem dữ liệu có tồn tại trước khi xóa
        val cursorBefore = db.rawQuery("SELECT * FROM $TABLE_CART WHERE UserID = ?", arrayOf(userId.toString()))
        cursorBefore.close()

        try {
            val rowsDeleted = db.delete(TABLE_CART, "UserID = ?", arrayOf(userId.toString()))

            _cartItems.clear() // Xóa giỏ hàng trên UI
        } catch (e: Exception) {
        } finally {
            // Kiểm tra sau khi xóa
            val cursorAfter = db.rawQuery("SELECT * FROM $TABLE_CART WHERE UserID = ?", arrayOf(userId.toString()))
            cursorAfter.close()

            db.close()
        }
    }
    fun addToCart(userId: Int, product: Product, quantity: Int) {
        val db = dbHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT SoLuong FROM $TABLE_CART WHERE UserID = ? AND TenSP = ?",
            arrayOf(userId.toString(), product.TenSP)
        )
        try {
        if (cursor.moveToFirst()) {
            val currentQuantity = cursor.getInt(0)
            val newQuantity = currentQuantity + quantity
            db.execSQL(
                "UPDATE $TABLE_CART SET SoLuong = ? WHERE UserID = ? AND TenSP = ?",
                arrayOf(newQuantity.toString(), userId.toString(), product.TenSP)
            )
        } else {
            val values = ContentValues().apply {
                put("UserID", userId)
                put("TenSP", product.TenSP)
                put("GiaTien", product.GiaTien)
                put("HinhAnh", product.HinhAnh)
                put("SoLuong", quantity)
            }
            db.insert(TABLE_CART, null, values)
        }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
            db.close()
        }
    }

    fun loadCartItems(userId: Int) {
        Log.d("CartDebug", "Starting to load cart items for user $userId")
        val db = dbHelper.openDatabase()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CART WHERE UserID = ?", arrayOf(userId.toString()))

        val itemCountInDb = cursor.count
        Log.d("CartDebug", "Found $itemCountInDb items in database for user $userId")

        Log.d("CartDebug", "Current cart size before clearing: ${_cartItems.size}")
        _cartItems.clear() // Xóa danh sách cũ trước khi thêm mới
        Log.d("CartDebug", "Cart cleared in memory, size now: ${_cartItems.size}")

        try {
            while (cursor.moveToNext()) {
                val productName = cursor.getString(cursor.getColumnIndexOrThrow("TenSP"))
                Log.d("CartDebug", "Loading cart item: $productName")

                val product = Product(
                    ID = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
                    TenSP = productName,
                    GiaTien = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaTien")),
                    HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")),
                    DanhMuc = "",
                    MoTa = "",
                    HinhAnh1 = null,
                    HinhAnh2 = null,
                    HinhAnh3 = null
                )

                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
                val cartItem = CartItem(
                    userId = userId,
                    product = product,
                    quantity = quantity
                )

                _cartItems.add(cartItem)
                Log.d("CartDebug", "Added to cart: $productName, quantity: $quantity")
            }

            Log.d("CartDebug", "Final cart size after loading: ${_cartItems.size}")
        } catch (e: Exception) {
            Log.e("CartDebug", "Error loading cart: ${e.message}", e)
        } finally {
            cursor.close()
            db.close()
            Log.d("CartDebug", "Database closed after loading cart")
        }
    }
    fun removeFromCart(userId: Int, productName: String) {
        val db = dbHelper.openDatabase()
        try {
            val rowsAffected = db.delete(TABLE_CART, "UserID = ? AND TenSP = ?", arrayOf(userId.toString(), productName))
            Log.d("CartDebug", "Removed $rowsAffected items from cart")
        } catch (e: Exception) {
            Log.e("CartDebug", "Error removing from cart: ${e.message}")
        } finally {
            db.close()
        }

        loadCartItems(userId) // ⚡ Cập nhật UI sau khi xóa sản phẩm
    }

    /** 📌 Tăng số lượng sản phẩm */
    fun increaseQuantity(userId: Int, cartItem: CartItem) {
        val db = dbHelper.openDatabase()
        val newQuantity = cartItem.quantity + 1

        try {
            db.execSQL(
                "UPDATE $TABLE_CART SET SoLuong = ? WHERE UserID = ? AND TenSP = ?",
                arrayOf(newQuantity.toString(), userId.toString(), cartItem.product.TenSP)
            )
        } catch (e: Exception) {
            Log.e("CartDebug", "Error increasing quantity: ${e.message}")
        } finally {
            db.close()
        }

        loadCartItems(userId) // ⚡ Cập nhật UI ngay lập tức
    }

    /** 📌 Giảm số lượng sản phẩm */
    fun decreaseQuantity(userId: Int, cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            val db = dbHelper.openDatabase()
            val newQuantity = cartItem.quantity - 1

            try {
                db.execSQL(
                    "UPDATE $TABLE_CART SET SoLuong = ? WHERE UserID = ? AND TenSP = ?",
                    arrayOf(newQuantity.toString(), userId.toString(), cartItem.product.TenSP)
                )
            } catch (e: Exception) {
                Log.e("CartDebug", "Error decreasing quantity: ${e.message}")
            } finally {
                db.close()
            }

            loadCartItems(userId) // ⚡ Cập nhật UI sau khi giảm số lượng
        }
    }
}

