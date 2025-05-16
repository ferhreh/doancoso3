package com.example.doancoso3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.doancoso3.ui.NotificationItem

class NotificationViewModel {
    // Danh sách thông báo có thể quan sát được
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    // Thêm thông báo mới
    fun addNotification(notification: NotificationItem) {
        _notifications.add(0, notification) // Thêm vào đầu danh sách để hiển thị mới nhất
    }

    // Đánh dấu thông báo đã đọc
    fun markAsRead(notificationId: String) {
        val index = _notifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val updatedNotification = _notifications[index].copy(isRead = true)
            _notifications[index] = updatedNotification
        }
    }

    // Xóa thông báo
    fun removeNotification(notificationId: String) {
        _notifications.removeIf { it.id == notificationId }
    }
}