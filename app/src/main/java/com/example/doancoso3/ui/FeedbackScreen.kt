package com.example.doancoso3.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.R
import com.example.doancoso3.data.FeedbackFirestoreRepository
import com.example.doancoso3.model.Feedback
import com.example.doancoso3.model.Product
import com.example.doancoso3.util.MediaUploadHelper
import com.example.doancoso3.util.MediaUploadHelper.copyUriToTempFile
import com.example.doancoso3.viewmodel.LanguageViewModel
import kotlinx.coroutines.launch

@Composable
fun FeedbackScreen(
    navController: NavController,
    product: Product,
    userId: String,
    userName: String,
    languageViewModel: LanguageViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val feedbackRepository = remember { FeedbackFirestoreRepository() }
    val language by languageViewModel.language.collectAsState()
    var rating by remember { mutableStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris
    }
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedVideo = it
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar with back button and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = if (language == "en") "Product Feedback" else "Đánh giá sản phẩm",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product image
                ShowImageFromAssets(product.HinhAnh)

                Spacer(modifier = Modifier.width(16.dp))

                // Product name
                Text(
                    text = product.TenSP,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rating section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = if (language == "en") "Rating" else "Đánh giá sao",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    for (i in 1..5) {
                        Image(
                            painter = painterResource(
                                id = if (i <= rating) R.drawable.star_on else R.drawable.star_off
                            ),
                            contentDescription = "Star $i",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Media upload section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (language == "en") "Add images and video about the product" else "Thêm hình ảnh và video về sản phẩm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Media selection cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Photo selection card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .padding(end = 8.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable {
                            photoPickerLauncher.launch("image/*")
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = "Camera",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hình ảnh",
                            fontSize = 14.sp
                        )
                    }
                }

                // Video selection card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .padding(start = 8.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable {
                            videoPickerLauncher.launch("video/*")
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.video),
                            contentDescription = "Video",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Video",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Display selected media previews
            if (selectedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (language == "en") "${selectedImages.size} image(s) selected" else "${selectedImages.size} hình ảnh đã chọn",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (selectedVideo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "en") "1 video selected" else "1 video đã chọn",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Feedback text input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (language == "en") "Feedback (minimum 10 words)" else "Đánh giá (tối thiểu 10 từ)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        text = if (language == "en") "Share your experience about this product..." else "Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Submit button
        Button(
            onClick = {
                if (feedbackText.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size >= 10) {
                    coroutineScope.launch {
                        // 1. Upload media
                        val uploadedImageUrls = mutableListOf<String>()
                        for (uri in selectedImages) {
                            Log.d("UPLOAD_IMAGE", "Preparing to upload image URI: $uri")
                            val bytes = uri.toByteArray(context)
                            if (bytes != null) {
                                val url = MediaUploadHelper.uploadImageFromBytes(bytes, userId)
                                Log.d("UPLOAD_IMAGE", "Upload result: $url")
                                if (url != null) {
                                    uploadedImageUrls.add(url)
                                } else {
                                    Log.e("UPLOAD_IMAGE", "Upload failed for uri: $uri")
                                }
                            } else {
                                Log.e("UPLOAD_IMAGE", "Failed to read bytes from uri: $uri")
                            }
                        }
                        val uploadedVideoUrl = selectedVideo?.let {
                            val safeUri = copyUriToTempFile(context, it)
                            if (safeUri != null) {
                                MediaUploadHelper.uploadVideoFromUri(safeUri, userId, context)
                            } else {
                                Log.e("UPLOAD_VIDEO", "Failed to copy video to temp file")
                                ""
                            }
                        } ?: ""

                        // 2. Tạo feedback
                        val feedback = Feedback(
                            userId = userId,
                            userName = userName,
                            productId = product.ID,
                            productName = product.TenSP,
                            rating = rating,
                            comment = feedbackText,
                            imageUrls = uploadedImageUrls,
                            videoUrl = uploadedVideoUrl,
                            timestamp = System.currentTimeMillis()
                        )

                        // 3. Lưu Firestore
                        val success = feedbackRepository.addFeedback(feedback)
                        if (success) {
                            // Show success (ví dụ: Toast)
                            navController.popBackStack()
                        } else {
                            // Show error message
                        }
                    }
                } else {
                    // Show error: tối thiểu 10 từ
                }
            },
            modifier = Modifier
                .height(56.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text(
                text = if (language == "en") "Submit Feedback" else "Gửi đánh giá",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ShowImageFromAssets(imageName: String) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeStream(context.assets.open("anh-man-hinh/$imageName"))?.asImageBitmap()

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image from Assets",
            modifier = Modifier
                .size(80.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

fun Uri.toByteArray(context: android.content.Context): ByteArray? {
    return try {
        context.contentResolver.openInputStream(this)?.use { input ->
            input.readBytes()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}