package com.example.doancoso3.util

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ImgurUploadHelper {
    private const val IMGUR_CLIENT_ID = "Client-ID 87d8a9e25a1fd6b"
    private const val IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun uploadImage(uri: Uri, context: Context): String? = uploadToImgur(uri, context, isVideo = false)

    suspend fun uploadVideo(uri: Uri, context: Context): String? = uploadToImgur(uri, context, isVideo = true)

    private suspend fun uploadToImgur(uri: Uri, context: Context, isVideo: Boolean): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()

            if (byteArray == null || byteArray.isEmpty()) {
                return@withContext null
            }

            val mimeType = context.contentResolver.getType(uri)
            val type = mimeType ?: if (isVideo) "video/mp4" else "image/jpeg"


            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "file", RequestBody.create(type.toMediaTypeOrNull(), byteArray))
                .build()

            val request = Request.Builder()
                .url(IMGUR_UPLOAD_URL)
                .addHeader("Authorization", IMGUR_CLIENT_ID)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            response.use {
                val responseBodyString = it.body?.string()

                if (it.isSuccessful && responseBodyString != null) {
                    val json = JSONObject(responseBodyString)
                    return@withContext json.getJSONObject("data").getString("link")
                } else {
                    Log.e("IMGUR_UPLOAD", "HTTP ${it.code}: $responseBodyString")
                }
            }
        } catch (e: Exception) {
            Log.e("IMGUR_UPLOAD", "Exception: ${e.localizedMessage}")
            e.printStackTrace()
        }
        return@withContext null
    }

}
