package com.example.doancoso3.data

import com.example.doancoso3.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addFavorite(product: Product, userId: String) {
        val data = hashMapOf(
            "productId" to product.ID,  // ✅ thêm dòng này
            "TenSP" to product.TenSP,
            "GiaTien" to product.GiaTien,
            "HinhAnh" to product.HinhAnh,
            "userID" to userId
        )
        db.collection("favorites")
            .document("${userId}_${product.ID}")
            .set(data)
            .await()
    }

    suspend fun getFavorites(userId: String): List<Product> {
        val snapshot = db.collection("favorites")
            .whereEqualTo("userID", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            Product(
                ID = doc.getString("productId") ?: "",
                DanhMuc = "",
                TenSP = doc.getString("TenSP") ?: "",
                GiaTien = (doc.getDouble("GiaTien") ?: 0.0).toLong(),
                HinhAnh = doc.getString("HinhAnh") ?: "",
                HinhAnh1 = "",
                HinhAnh2 = "",
                HinhAnh3 = "",
                MoTa = "",
                userId = doc.getString("userID") ?: ""
            )
        }
    }

    suspend fun removeFavorite(userId: String, productId: String) {
        val docId = "${userId}_${productId}"
        FirebaseFirestore.getInstance()
            .collection("favorites")
            .document(docId)
            .delete()
            .await()
    }
}
