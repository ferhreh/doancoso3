package com.example.doancoso3.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: List<CartItem> get() = _cartItems.value

    var isLoading = mutableStateOf(false)
        private set

    private var hasLoadedOnce = false

    fun loadCartItems(userId: String, forceReload: Boolean = false) {
        if (isLoading.value || (hasLoadedOnce && !forceReload)) return
        isLoading.value = true

        db.collection("carts").document(userId).collection("items")
            .get()
            .addOnSuccessListener { result ->
                val newItems = result.mapNotNull { doc ->
                    val productMap = doc.get("product") as? Map<*, *> ?: return@mapNotNull null
                    val product = Product(
                        ID = doc.id,
                        TenSP = productMap["TenSP"] as? String ?: "",
                        GiaTien = (productMap["GiaTien"] as? Number)?.toLong() ?: 0L,
                        HinhAnh = productMap["HinhAnh"] as? String ?: "",
                        DanhMuc = "", MoTa = "", HinhAnh1 = "", HinhAnh2 = "", HinhAnh3 = ""
                    )
                    val quantity = (doc.getLong("quantity") ?: 0L).toInt()
                    CartItem(
                        userId = userId,
                        product = product,
                        productId = product.ID,
                        productName = product.TenSP,
                        productImage = product.HinhAnh,
                        productPrice = product.GiaTien.toDouble(),
                        quantity = quantity
                    )
                }
                _cartItems.value = newItems
                hasLoadedOnce = true
            }
            .addOnFailureListener {
                Log.e("CartViewModel", "Lỗi khi load giỏ hàng", it)
            }
            .addOnCompleteListener {
                isLoading.value = false
            }
    }

    fun addToCart(userId: String, product: Product, quantity: Int = 1) {
        val itemRef = db.collection("carts").document(userId)
            .collection("items").document(product.ID)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(itemRef)
            val newQuantity = if (snapshot.exists()) {
                val currentQty = snapshot.getLong("quantity") ?: 0
                currentQty + quantity
            } else {
                quantity
            }

            val data = mapOf(
                "product" to mapOf(
                    "TenSP" to product.TenSP,
                    "GiaTien" to product.GiaTien,
                    "HinhAnh" to product.HinhAnh
                ),
                "quantity" to newQuantity
            )
            transaction.set(itemRef, data)
        }.addOnSuccessListener {
            loadCartItems(userId, forceReload = true)
        }.addOnFailureListener {
            Log.e("CartViewModel", "Lỗi khi thêm vào giỏ hàng", it)
        }
    }

    fun increaseQuantity(userId: String, cartItem: CartItem) {
        val itemRef = db.collection("carts").document(userId)
            .collection("items").document(cartItem.product.ID)

        val newQuantity = cartItem.quantity + 1
        itemRef.update("quantity", newQuantity)
            .addOnSuccessListener {
                // ✅ Chỉ update local để tránh reload
                _cartItems.value = _cartItems.value.map {
                    if (it.productId == cartItem.productId) it.copy(quantity = newQuantity) else it
                }
            }
            .addOnFailureListener {
                Log.e("CartViewModel", "Lỗi khi tăng số lượng", it)
            }
    }

    fun decreaseQuantity(userId: String, cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            val itemRef = db.collection("carts").document(userId)
                .collection("items").document(cartItem.product.ID)

            val newQuantity = cartItem.quantity - 1
            itemRef.update("quantity", newQuantity)
                .addOnSuccessListener {
                    _cartItems.value = _cartItems.value.map {
                        if (it.productId == cartItem.productId) it.copy(quantity = newQuantity) else it
                    }
                }
                .addOnFailureListener {
                    Log.e("CartViewModel", "Lỗi khi giảm số lượng", it)
                }
        }
    }

    private fun updateQuantity(userId: String, productId: String, newQuantity: Int) {
        val itemRef = db.collection("carts").document(userId)
            .collection("items").document(productId)

        itemRef.update("quantity", newQuantity)
            .addOnSuccessListener { loadCartItems(userId, forceReload = true) }
            .addOnFailureListener {
                Log.e("CartViewModel", "Lỗi khi cập nhật số lượng", it)
            }
    }

    fun removeFromCart(userId: String, productId: String) {
        db.collection("carts").document(userId)
            .collection("items").document(productId).delete()
            .addOnSuccessListener {
                loadCartItems(userId, forceReload = true)
            }
            .addOnFailureListener {
                Log.e("CartViewModel", "Lỗi khi xóa sản phẩm", it)
            }
    }

    fun clearCart(userId: String) {
        val cartRef = db.collection("carts").document(userId).collection("items")
        cartRef.get().addOnSuccessListener { result ->
            val batch = db.batch()
            for (doc in result.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                _cartItems.value = emptyList()
            }.addOnFailureListener {
                Log.e("CartViewModel", "Lỗi khi xóa toàn bộ giỏ hàng", it)
            }
        }
    }
}
