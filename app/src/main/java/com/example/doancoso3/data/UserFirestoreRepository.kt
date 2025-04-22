package com.example.doancoso3.data

import com.example.doancoso3.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun addUser(user: User) {
        usersCollection.document(user.id.toString()).set(user).await()
    }

    suspend fun checkUser(email: String, password: String): Boolean {
        val querySnapshot = usersCollection
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()
        return !querySnapshot.isEmpty
    }

    suspend fun getUserId(email: String, password: String): String? {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .await()

        return if (!snapshot.isEmpty) {
            snapshot.documents[0].id // Đây chính là document ID
        } else {
            null
        }
    }

    suspend fun getUserById(userId: String): User? {
        val snapshot = usersCollection.document(userId.toString()).get().await()
        return snapshot.toObject(User::class.java)
    }
    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }
}
