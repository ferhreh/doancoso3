package com.example.doancoso3.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

object MediaUploadHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun uploadImageFromBytes(bytes: ByteArray, userId: String): String? {
        return try {
            val fileName = "${userId}_${System.currentTimeMillis()}.jpg"
            val imageRef = storageRef.child("feedback_images/$fileName")
            Log.d("UPLOAD_IMAGE", "Uploading image as: $fileName")
            imageRef.putBytes(bytes).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun uploadVideoFromUri(uri: Uri, userId: String, context: Context): String? {
        return try {
            val videoRef = storageRef.child("feedback_videos/${userId}_${System.currentTimeMillis()}.mp4")
            Log.d("UPLOAD_VIDEO", "Uploading from URI: $uri")
            videoRef.putFile(uri).await()
            val downloadUrl = videoRef.downloadUrl.await().toString()
            Log.d("UPLOAD_VIDEO", "Upload success: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e("UPLOAD_VIDEO", "Upload failed: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    fun copyUriToTempFile(context: Context, uri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".mp4", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}