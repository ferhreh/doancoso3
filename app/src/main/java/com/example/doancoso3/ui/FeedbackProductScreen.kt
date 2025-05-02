package com.example.doancoso3.ui

// FeedbackProductScreen.kt
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.doancoso3.R
import com.example.doancoso3.data.FeedbackFirestoreRepository
import com.example.doancoso3.model.Feedback
import com.example.doancoso3.viewmodel.LanguageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("DefaultLocale")
@Composable
fun FeedbackProductScreen(
    productId: String,
    navController: NavController,
    languageViewModel: LanguageViewModel
) {
    val language by languageViewModel.language.collectAsState()
    val context = LocalContext.current
    val feedbackRepository = remember { FeedbackFirestoreRepository() }
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var feedbacks by remember { mutableStateOf<List<Feedback>>(emptyList()) }
    var filteredFeedbacks by remember { mutableStateOf<List<Feedback>>(emptyList()) }
    var selectedMedia by remember { mutableStateOf<MediaItemData?>(null) }
    var activeFilter by remember { mutableStateOf(0) } // 0 = All, 1-5 = Star ratings

    // Rating distribution
    var ratingCounts by remember { mutableStateOf(IntArray(5)) }
    var averageRating by remember { mutableStateOf(0f) }

    // Load feedbacks
    LaunchedEffect(productId) {
        try {
            val result = withContext(Dispatchers.IO) {
                feedbackRepository.getFeedbacksByProductId(productId)
            }

            feedbacks = result
            filteredFeedbacks = result

            // Calculate rating distribution
            val counts = IntArray(5)
            result.forEach { feedback ->
                if (feedback.rating in 1..5) {
                    counts[feedback.rating - 1]++
                }
            }
            ratingCounts = counts

            // Calculate average rating
            averageRating = if (result.isNotEmpty()) {
                result.sumOf { it.rating.toDouble() }.toFloat() / result.size
            } else {
                0f
            }

            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(
                context,
                if (language == "vi") "Lỗi khi tải đánh giá" else "Error loading feedbacks",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Filter by rating function
    fun filterByRating(rating: Int) {
        activeFilter = rating
        filteredFeedbacks = if (rating == 0) {
            feedbacks
        } else {
            feedbacks.filter { it.rating == rating }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (language == "en") "Feedback" else "Đánh giá",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Summary Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(2.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // Nền trắng
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Average rating
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = String.format("%.1f", averageRating),
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row {
                                        repeat(5) { index ->
                                            Icon(
                                                painter = painterResource(
                                                    id = if (index < averageRating.toInt()) R.drawable.star_on
                                                    else R.drawable.star_off
                                                ),
                                                contentDescription = null,
                                                tint = if (index < averageRating.toInt()) Color(0xFFFFC107)
                                                else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "(${feedbacks.size})",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Rating distribution
                                Column(
                                    modifier = Modifier.weight(2f)
                                ) {
                                    for (i in 5 downTo 1) {
                                        val count = ratingCounts[i - 1]
                                        val percentage = if (feedbacks.isNotEmpty()) {
                                            count.toFloat() / feedbacks.size
                                        } else {
                                            0f
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$i",
                                                fontSize = 12.sp,
                                                modifier = Modifier.width(16.dp)
                                            )
                                            Icon(
                                                painter = painterResource(id = R.drawable.star_on),
                                                contentDescription = null,
                                                tint = Color(0xFFFFC107),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.LightGray)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(percentage)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFFFC107))
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = count.toString(),
                                                fontSize = 12.sp,
                                                modifier = Modifier.width(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Filter buttons
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(2.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // Nền trắng
                        )
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterButton(
                                    text = if (language == "vi") "Tất cả" else "All",
                                    isSelected = activeFilter == 0,
                                    onClick = { filterByRating(0) }
                                )
                            }

                            items(5) { index ->
                                val rating = 5 - index
                                FilterButton(
                                    text = "$rating ★",
                                    isSelected = activeFilter == rating,
                                    onClick = { filterByRating(rating) }
                                )
                            }
                        }
                    }
                }

                // No reviews message
                if (filteredFeedbacks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == "vi") "Không có đánh giá nào" else "No reviews found",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Feedback items
                items(filteredFeedbacks) { feedback ->
                    FeedbackItem(
                        feedback = feedback,
                        onMediaClick = { mediaItem ->
                            selectedMedia = mediaItem
                        }
                    )
                }
            }
        }
    }

    // Media preview dialog
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
                    is MediaItemData.Video -> AndroidView(
                        factory = { context ->
                            PlayerView(context).apply {
                                player = ExoPlayer.Builder(context).build().apply {
                                    setMediaItem(MediaItem.fromUri(media.url))
                                    prepare()
                                    playWhenReady = true
                                }
                                useController = true
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFFC107) else Color(0xFFEEEEEE),
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun FeedbackItem(
    feedback: Feedback,
    onMediaClick: (MediaItemData) -> Unit
) {
    val mediaItems = mutableListOf<MediaItemData>()

    // Convert images and video to MediaItemData
    feedback.imageUrls.forEach { url ->
        mediaItems.add(MediaItemData.Image(url))
    }

    if (feedback.videoUrl.isNotEmpty()) {
        mediaItems.add(MediaItemData.Video(feedback.videoUrl))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(2.dp))
            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Nền trắng
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar (first letter)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = feedback.userName.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = feedback.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Chuyển đổi timestamp thành chuỗi ngày
                    val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(feedback.timestamp))

                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating stars
            Row {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(
                            id = if (index < feedback.rating) R.drawable.star_on
                            else R.drawable.star_off
                        ),
                        contentDescription = null,
                        tint = if (index < feedback.rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Feedback text
            if (feedback.comment.isNotBlank()) {
                Text(
                    text = feedback.comment,
                    fontSize = 14.sp
                )
            }

            // Media (images or video)
            if (mediaItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mediaItems) { media ->
                        when (media) {
                            is MediaItemData.Image -> {
                                Image(
                                    painter = rememberAsyncImagePainter(media.url),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onMediaClick(media) },
                                    contentScale = ContentScale.Crop
                                )
                            }

                            is MediaItemData.Video -> {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black)
                                        .clickable { onMediaClick(media) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
