package com.example.doancoso3.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import com.example.doancoso3.data.OrderFirestoreRepository
import com.example.doancoso3.model.Feedback
import com.example.doancoso3.model.Order
import com.example.doancoso3.model.Product
import com.example.doancoso3.util.ImgurUploadHelper
import com.example.doancoso3.util.NotificationViewModelProvider
import com.example.doancoso3.viewmodel.LanguageViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun FeedbackScreen(
    navController: NavController,
    product: Product,
    userId: String,
    userName: String,
    orderId: String,
    languageViewModel: LanguageViewModel,
    order: Order
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
    val uploadedImageUrls = remember { mutableStateListOf<String>() }
    var uploadedVideoUrl by remember { mutableStateOf<String?>(null) }
    val orderRepository = OrderFirestoreRepository()
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
            // Hiển thị hình ảnh
            if (selectedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (language == "en") "${selectedImages.size} image(s) selected" else "${selectedImages.size} hình ảnh đã chọn",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Hiển thị video
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
                        uploadedImageUrls.clear()

                        // Upload ảnh
                        for (uri in selectedImages) {
                            val url = ImgurUploadHelper.uploadImage(uri, context)
                            if (url != null) {
                                uploadedImageUrls.add(url)
                            } else {
                                Log.e("UPLOAD_IMAGE", "Failed: $uri")
                            }
                        }
                        // Upload video nếu có
                        uploadedVideoUrl = selectedVideo?.let { videoUri ->
                            ImgurUploadHelper.uploadVideo(videoUri, context)
                        }

                        // Tạo đối tượng feedback
                        val feedback = Feedback(
                            userId = userId,
                            userName = userName,
                            productId = product.ID,
                            productName = product.TenSP,
                            rating = rating,
                            comment = feedbackText,
                            imageUrls = uploadedImageUrls.toList(),
                            videoUrl = uploadedVideoUrl ?: "",
                            timestamp = System.currentTimeMillis()
                        )

                        // Gửi lên Firestore
                        val success = feedbackRepository.addFeedback(feedback)
                        if (success) {
                            orderRepository.markOrderAsReviewed(userId, orderId)
                            Toast.makeText(
                                context,
                                if (language == "en") "Feedback submitted successfully!" else "Đã gửi đánh giá thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        } else {
                            Log.e("UPLOAD_FEEDBACK", "Failed to upload feedback to Firestore")
                        }
                    }
                } else {
                    Log.w("FEEDBACK_VALIDATE", "Feedback phải có ít nhất 10 từ")
                }
                NotificationViewModelProvider.getInstance().addNotification(
                    NotificationItem(
                        id = UUID.randomUUID().toString(),
                        type = NotificationType.FEEDBACK_SENT,
                        productName = product.TenSP,
                        timestamp = System.currentTimeMillis()
                    )
                )
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