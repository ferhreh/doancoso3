package com.example.doancoso3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.viewmodel.LanguageViewModel
import com.example.doancoso3.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    navController: NavController,
    userId: String,
    languageViewModel: LanguageViewModel,
    notificationViewModel: NotificationViewModel
) {
    val language by languageViewModel.language.collectAsState()
    val notifications = notificationViewModel.notifications




    // Color definition
    val backgroundColor = Color(0xFFF5F7FA)
    val cardBackgroundColor = Color.White
    val textPrimaryColor = Color(0xFF212121)
    val textSecondaryColor = Color(0xFF757575)
    val unreadIndicatorColor = Color(0xFF2196F3)

    // Group notifications by day
    val notificationsByDay = remember(notifications) {
        notifications.groupBy { notification ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = notification.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = cardBackgroundColor,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF5F7FA), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimaryColor
                    )
                }
                Text(
                    text = if (language == "en") "Notifications" else "Thông báo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        // If no notifications
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (language == "en") "No notifications yet" else "Chưa có thông báo nào",
                        fontSize = 18.sp,
                        color = textSecondaryColor
                    )
                }
            }
        } else {
            // Notification list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                notificationsByDay.forEach { (day, notificationsForDay) ->
                    item {
                        val dayText = getDayText(day, language)
                        Text(
                            text = dayText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondaryColor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(notificationsForDay) { notification ->
                        NotificationCard(
                            notification = notification,
                            language = language,
                            onNotificationClick = {
                                // Đánh dấu thông báo là đã đọc khi click
                                notificationViewModel.markAsRead(notification.id)
                            },
                            unreadIndicatorColor = unreadIndicatorColor,
                            cardBackgroundColor = cardBackgroundColor,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun NotificationCard(
    notification: NotificationItem,
    language: String,
    onNotificationClick: (NotificationItem) -> Unit,
    unreadIndicatorColor: Color,
    cardBackgroundColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onNotificationClick(notification) },
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(notification.type.getBackgroundColor().copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.getIcon(),
                    contentDescription = null,
                    tint = notification.type.getBackgroundColor(),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Notification content
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = notification.getTitle(language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.getMessage(language),
                    fontSize = 14.sp,
                    color = textSecondaryColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getTimeAgo(notification.timestamp, language),
                    fontSize = 12.sp,
                    color = textSecondaryColor.copy(alpha = 0.7f)
                )
            }

            // Unread indicator
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(unreadIndicatorColor, CircleShape)
                )
            }
        }
    }
}

// Helper function to get formatted day text
fun getDayText(timestamp: Long, language: String): String {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val date = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    return when (timestamp) {
        today.timeInMillis -> if (language == "en") "Today" else "Hôm nay"
        yesterday.timeInMillis -> if (language == "en") "Yesterday" else "Hôm qua"
        else -> {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

// Helper function to get time ago text
fun getTimeAgo(timestamp: Long, language: String): String {
    val now = System.currentTimeMillis()
    val diffInMillis = now - timestamp

    val seconds = diffInMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> {
            if (days == 1L) {
                if (language == "en") "1 day ago" else "1 ngày trước"
            } else {
                if (language == "en") "$days days ago" else "$days ngày trước"
            }
        }
        hours > 0 -> {
            if (hours == 1L) {
                if (language == "en") "1 hour ago" else "1 giờ trước"
            } else {
                if (language == "en") "$hours hours ago" else "$hours giờ trước"
            }
        }
        minutes > 0 -> {
            if (minutes == 1L) {
                if (language == "en") "1 minute ago" else "1 phút trước"
            } else {
                if (language == "en") "$minutes minutes ago" else "$minutes phút trước"
            }
        }
        else -> if (language == "en") "Just now" else "Vừa xong"
    }
}

// Notification data class
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val productName: String = "",
    val timestamp: Long,
    val isRead: Boolean = false
) {
    fun getTitle(language: String): String {
        return when (type) {
            NotificationType.PASSWORD_CHANGED -> if (language == "en") "Password Updated" else "Cập nhật mật khẩu"
            NotificationType.FEEDBACK_SENT -> if (language == "en") "Feedback Sent" else "Đã gửi đánh giá"
            NotificationType.ORDER_PLACED -> if (language == "en") "Order Placed" else "Đặt hàng thành công"
            NotificationType.ORDER_DELIVERED -> if (language == "en") "Order Delivered" else "Đơn hàng đã giao"
            NotificationType.ORDER_CANCELLED -> if (language == "en") "Order Cancelled" else "Đơn hàng đã hủy"
        }
    }

    fun getMessage(language: String): String {
        return when (type) {
            NotificationType.PASSWORD_CHANGED -> {
                if (language == "en")
                    "Your account password has been successfully updated."
                else
                    "Mật khẩu tài khoản của bạn đã được cập nhật thành công."
            }
            NotificationType.FEEDBACK_SENT -> {
                if (language == "en")
                    "Thank you for your feedback on $productName. Your review is now visible to others."
                else
                    "Cảm ơn bạn đã đánh giá sản phẩm $productName. Đánh giá của bạn hiện đã được hiển thị."
            }
            NotificationType.ORDER_PLACED -> {
                if (language == "en")
                    "Your order for $productName has been successfully placed. We're preparing your package."
                else
                    "Đơn hàng $productName của bạn đã được đặt thành công. Chúng tôi đang chuẩn bị gói hàng của bạn."
            }
            NotificationType.ORDER_DELIVERED -> {
                if (language == "en")
                    "Your order for $productName has been delivered. Enjoy your purchase!"
                else
                    "Đơn hàng $productName của bạn đã được giao thành công. Chúc bạn hài lòng với sản phẩm!"
            }
            NotificationType.ORDER_CANCELLED -> {
                if (language == "en")
                    "Your order for $productName has been cancelled."
                else
                    "Đơn hàng $productName của bạn đã bị hủy."
            }
        }
    }
}

// Notification types
enum class NotificationType {
    PASSWORD_CHANGED,
    FEEDBACK_SENT,
    ORDER_PLACED,
    ORDER_DELIVERED,
    ORDER_CANCELLED;

    fun getIcon(): ImageVector {
        return when (this) {
            PASSWORD_CHANGED -> Icons.Default.Lock
            FEEDBACK_SENT -> Icons.Default.Star
            ORDER_PLACED -> Icons.Default.ShoppingCart
            ORDER_DELIVERED -> Icons.Default.LocalShipping
            ORDER_CANCELLED -> Icons.Default.Cancel
        }
    }

    fun getBackgroundColor(): Color {
        return when (this) {
            PASSWORD_CHANGED -> Color(0xFF673AB7) // Deep Purple
            FEEDBACK_SENT -> Color(0xFFFFC107)    // Amber
            ORDER_PLACED -> Color(0xFF4CAF50)     // Green
            ORDER_DELIVERED -> Color(0xFF2196F3)  // Blue
            ORDER_CANCELLED -> Color(0xFFF44336)  // Red
        }
    }
}