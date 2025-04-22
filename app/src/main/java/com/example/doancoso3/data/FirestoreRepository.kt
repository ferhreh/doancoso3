package com.example.doancoso3.data

import android.util.Log
import com.example.doancoso3.model.Product
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    fun addFavorite(product: Product, userId: Int) {
        val favoriteData = hashMapOf(
            "userId" to userId,
            "TenSP" to product.TenSP,
            "GiaTien" to product.GiaTien,
            "HinhAnh" to product.HinhAnh
        )

        db.collection("favorites")
            .add(favoriteData)
            .addOnSuccessListener {
                Log.d("Firestore", "Thêm sản phẩm yêu thích thành công.")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Thêm sản phẩm yêu thích thất bại: ${it.message}")
            }
    }

    fun getFavorites(userId: Int, callback: (List<Product>) -> Unit) {
        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map { doc ->
                    Product(
                        ID = doc.id,// tạm tạo ID, nếu có thể lưu id sản phẩm gốc thì dùng
                        DanhMuc = "",
                        TenSP = doc.getString("TenSP") ?: "",
                        GiaTien = (doc.getDouble("GiaTien") ?: 0.0).toLong(),
                        HinhAnh = doc.getString("HinhAnh") ?: "",
                        HinhAnh1 = "",
                        HinhAnh2 = "",
                        HinhAnh3 = "",
                        MoTa = ""
                    )
                }
                callback(list)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Lỗi khi lấy favorites: ${it.message}")
                callback(emptyList())
            }
    }

    fun removeFavorite(product: Product, userId: Int) {
        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .whereEqualTo("TenSP", product.TenSP)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    db.collection("favorites").document(doc.id).delete()
                }
                Log.d("Firestore", "Xóa sản phẩm yêu thích thành công.")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Xóa sản phẩm yêu thích thất bại: ${it.message}")
            }
    }
}
