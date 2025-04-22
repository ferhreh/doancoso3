package com.example.doancoso3.data

import com.example.doancoso3.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products") // sửa lại tên collection khớp Firestore

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(ID = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getProductById(productId: String): Product? {
        return try {
            val doc = productsCollection.document(productId).get().await()
            doc.toObject(Product::class.java)?.copy(ID = doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
