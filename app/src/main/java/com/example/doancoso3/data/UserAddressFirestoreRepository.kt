package com.example.doancoso3.data

import com.example.doancoso3.model.UserAddress
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserAddressFirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // Add a new address to the user's subcollection
    suspend fun addUserAddress(userId: String, name: String, address: String, phoneNumber: String) {
        val addressRef = db.collection("user_addresses")
            .document(userId)
            .collection("addresses")
            .document() // Auto-generate document ID

        val newAddress = UserAddress(
            id = addressRef.id,
            name = name,
            address = address,
            phone = phoneNumber
        )

        addressRef.set(newAddress).await()
    }

    // Update an existing address
    suspend fun updateUserAddress(userId: String, userAddress: UserAddress): Boolean {
        return try {
            db.collection("user_addresses")
                .document(userId)
                .collection("addresses")
                .document(userAddress.id)
                .set(userAddress, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Get all addresses for a user
    suspend fun getUserAddresses(userId: String): List<UserAddress> {
        return try {
            val snapshot = db.collection("user_addresses")
                .document(userId)
                .collection("addresses")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(UserAddress::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Delete a specific address
    suspend fun deleteUserAddress(userId: String, addressId: String): Boolean {
        return try {
            db.collection("user_addresses")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
