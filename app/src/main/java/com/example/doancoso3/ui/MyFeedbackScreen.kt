package com.example.doancoso3.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.doancoso3.R
import com.example.doancoso3.data.FeedbackFirestoreRepository
import com.example.doancoso3.model.Feedback
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.LanguageViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.doancoso3.data.ProductFirestoreRepository
sealed class MediaItemData {
    data class Image(val url: String) : MediaItemData()
    data class Video(val url: String) : MediaItemData()
}

@Composable
fun MyFeedbackScreen(
    navController: NavController,
    userId: String,
    languageViewModel: LanguageViewModel,
) {
    val language by languageViewModel.language.collectAsState()
    val feedbackRepository = remember { FeedbackFirestoreRepository() }
    val productRepository = remember { ProductFirestoreRepository() }

    var feedbacks by remember { mutableStateOf<List<Feedback>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        isLoading = true
        feedbacks = feedbackRepository.getFeedbacksByUserId(userId)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (language == "en") "My Feedback" else "Đánh giá của tôi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (feedbacks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == "en") "You haven't posted any reviews yet" else "Bạn chưa đăng đánh giá nào",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feedbacks) { feedback ->
                    var product by remember { mutableStateOf<Product?>(null) }

                    LaunchedEffect(feedback.productId) {
                        product = productRepository.getProductById(feedback.productId)
                    }

                    product?.let {
                        FeedbackItem(item = it, feedback = feedback, language = language)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackItem(item: Product, feedback: Feedback, language: String) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = remember(feedback.timestamp) {
        dateFormat.format(Date(feedback.timestamp))
    }

    val mediaItems = remember(feedback.imageUrls, feedback.videoUrl) {
        val list = mutableListOf<MediaItemData>()
        list.addAll(feedback.imageUrls.map { MediaItemData.Image(it) })
        if (feedback.videoUrl.isNotEmpty()) list.add(MediaItemData.Video(feedback.videoUrl))
        list
    }

    var selectedMedia by remember { mutableStateOf<MediaItemData?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShowImageFromAssetsss(item.HinhAnh)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = feedback.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f))
                        .border(1.dp, MaterialTheme.colors.primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = feedback.userName.take(1).uppercase(),
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = feedback.userName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        for (i in 1..5) {
                            Image(
                                painter = painterResource(
                                    id = if (i <= feedback.rating) R.drawable.star_on else R.drawable.star_off
                                ),
                                contentDescription = "Star $i",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = feedback.comment, fontSize = 15.sp, lineHeight = 24.sp)

            if (mediaItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mediaItems) { media ->
                        when (media) {
                            is MediaItemData.Image -> Image(
                                painter = rememberAsyncImagePainter(media.url),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedMedia = media },
                                contentScale = ContentScale.Crop
                            )

                            is MediaItemData.Video -> Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedMedia = media },
                                elevation = 4.dp
                            ) {
                                AndroidView(factory = {
                                    PlayerView(it).apply {
                                        player = ExoPlayer.Builder(it).build().apply {
                                            setMediaItem(MediaItem.fromUri(media.url))
                                            prepare()
                                            playWhenReady = false
                                        }
                                        useController = false
                                    }
                                })
                            }
                        }
                    }
                }
            }

            selectedMedia?.let { media ->
                Dialog(onDismissRequest = { selectedMedia = null }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                            .background(Color.Black, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        when (media) {
                            is MediaItemData.Image -> Image(
                                painter = rememberAsyncImagePainter(media.url),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            is MediaItemData.Video -> AndroidView(factory = {
                                PlayerView(it).apply {
                                    player = ExoPlayer.Builder(it).build().apply {
                                        setMediaItem(MediaItem.fromUri(media.url))
                                        prepare()
                                        playWhenReady = true
                                    }
                                    useController = true
                                }
                            }, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowImageFromAssetsss(imageName: String) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeStream(context.assets.open("anh-man-hinh/$imageName"))?.asImageBitmap()

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image from Assets",
            modifier = Modifier
                .size(width = 30.dp, height = 30.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
