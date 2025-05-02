package com.example.doancoso3.data

import android.util.Log
import com.example.doancoso3.model.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FeedbackFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val feedbacksCollection = db.collection("feedbacks")

    suspend fun addFeedback(feedback: Feedback): Boolean {
        return try {
            val feedbackMap = hashMapOf(
                "userId" to feedback.userId,
                "userName" to feedback.userName,
                "productId" to feedback.productId,
                "productName" to feedback.productName,
                "rating" to feedback.rating,
                "comment" to feedback.comment,
                "imageUrls" to feedback.imageUrls,
                "videoUrl" to feedback.videoUrl,
                "timestamp" to feedback.timestamp
            )
            feedbacksCollection.document(feedback.userId).set(hashMapOf("exists" to true)).await()
            feedbacksCollection
                .document(feedback.userId)
                .collection("feedback_items")
                .add(feedbackMap)
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getFeedbacksByProductId(productId: String): List<Feedback> {
        val feedbackList = mutableListOf<Feedback>()

        try {
            // Log đầu vào
            Log.d("FeedbackDebug", "Bắt đầu load feedbacks cho productId: '$productId'")

            // Lấy tất cả document trong collection "feedbacks" (mỗi document là 1 userId)
            val usersSnapshot = db.collection("feedbacks").get().await()

            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                Log.d("FeedbackDebug", "Đang kiểm tra userId: $userId")

                val feedbacksSnapshot = feedbacksCollection
                    .document(userId)
                    .collection("feedback_items")
                    .whereEqualTo("productId", productId.trim()) // đề phòng có khoảng trắng
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                Log.d("FeedbackDebug", "Tìm thấy ${feedbacksSnapshot.size()} feedback từ user $userId cho sản phẩm $productId")

                for (doc in feedbacksSnapshot.documents) {
                    try {
                        val feedback = Feedback(
                            id = doc.id,
                            userId = doc["userId"] as String,
                            userName = doc["userName"] as String,
                            productId = doc["productId"] as String,
                            productName = doc["productName"] as String,
                            rating = (doc["rating"] as Long).toInt(),
                            comment = doc["comment"] as String,
                            imageUrls = (doc["imageUrls"] as? List<String>) ?: emptyList(),
                            videoUrl = (doc["videoUrl"] as? String) ?: "",
                            timestamp = doc["timestamp"] as Long
                        )
                        feedbackList.add(feedback)
                    } catch (e: Exception) {
                        Log.e("FeedbackDebug", "Lỗi khi parse 1 feedback: ${e.message}")
                    }
                }
            }

            Log.d("FeedbackDebug", "Tổng số feedback cho sản phẩm $productId: ${feedbackList.size}")
        } catch (e: Exception) {
            Log.e("FeedbackDebug", "Lỗi khi load feedbacks: ${e.message}")
        }

        return feedbackList
    }

    suspend fun getFeedbacksByUserId(userId: String): List<Feedback> {
        return try {
            val snapshot = feedbacksCollection
                .document(userId)
                .collection("feedback_items")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    Feedback(
                        id = doc.id,
                        userId = doc["userId"] as String,
                        userName = doc["userName"] as String,
                        productId = doc["productId"] as String,
                        productName = doc["productName"] as String,
                        rating = (doc["rating"] as Long).toInt(),
                        comment = doc["comment"] as String,
                        imageUrls = (doc["imageUrls"] as? List<String>) ?: emptyList(),
                        videoUrl = (doc["videoUrl"] as? String) ?: "",
                        timestamp = doc["timestamp"] as Long
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
