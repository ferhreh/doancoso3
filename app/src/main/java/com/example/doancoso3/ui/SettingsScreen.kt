package com.example.doancoso3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.data.UserFirestoreRepository
import com.example.doancoso3.model.User
import com.example.doancoso3.viewmodel.LanguageViewModel
import com.example.doancoso3.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    navController: NavController,
    userId: String,
    languageViewModel: LanguageViewModel,
    notificationViewModel: NotificationViewModel
) {
    val userRepository = remember { UserFirestoreRepository() }
    val coroutineScope = rememberCoroutineScope()

    // User data state
    var user by remember { mutableStateOf<User?>(null) }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    // Password states
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordSuccess by remember { mutableStateOf(false) }

    // Notification state
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Language selection
    val language by languageViewModel.language.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    //

    // Load user data on initial composition
    LaunchedEffect(userId) {
        try {
            val userData = userRepository.getUserById(userId)
            if (userData != null) {
                user = userData
                userName = userData.name ?: ""
                userEmail = userData.email ?: ""
            }
        } finally {
            isLoading = false
        }
    }

    // Show dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Update password function
    fun updatePassword(language: String) {
        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            passwordError = if (language == "en") "Please fill in all fields" else "Vui lòng nhập đầy đủ thông tin"
            return
        }

        coroutineScope.launch {
            val isPasswordCorrect = userRepository.checkUser(userEmail, currentPassword)
            if (isPasswordCorrect) {
                // Update the user's password
                user?.let {
                    val updatedUser = it.copy(id = it.id ?: userId, password = newPassword)
                    userRepository.addUser(updatedUser)

                    // Hiển thị thông báo thành công ngay tại chỗ
                    passwordSuccess = true
                    passwordError = null
                    currentPassword = ""
                    newPassword = ""

                    // Tạo notification mới
                    val notification = NotificationItem(
                        id = System.currentTimeMillis().toString(),
                        type = NotificationType.PASSWORD_CHANGED,
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )

                    // Thêm vào NotificationViewModel
                    notificationViewModel.addNotification(notification)
                }
            } else {
                passwordError = if (language == "en") "Current password is incorrect" else "Mật khẩu hiện tại không chính xác"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top app bar
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
                text = if (language == "en") "Settings" else "Cài đặt",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Personal Information Section
            Text(
                text = if (language == "en") "Personal Information" else "Thông tin cá nhân",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(2.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // Nền trắng
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Name
                    Text(
                        text = if (language == "en") "Full Name" else "Họ và tên",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Password Section
            Text(
                text = if (language == "en") "Password" else "Mật khẩu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Card(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(2.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // Nền trắng
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Current Password
                    Text(
                        text = if (language == "en") "Current Password" else "Mật khẩu hiện tại",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            passwordError = null
                            passwordSuccess = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // New Password
                    Text(
                        text = if (language == "en") "New Password" else "Mật khẩu mới",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            passwordError = null
                            passwordSuccess = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (passwordSuccess) {
                        Text(
                            text = if (language == "en") "Password updated successfully!" else "Mật khẩu đã được cập nhật thành công!",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { updatePassword(language) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = if (language == "en") "Update Password" else "Cập nhật mật khẩu")
                    }
                }
            }

            // Language Selection
            Text(
                text = if (language == "en") "Language" else "Ngôn ngữ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(2.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                onClick = { showLanguageDialog = true },
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (language == "en") "Application language" else "Ngôn ngữ ứng dụng", fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (language == "vi") "Tiếng Việt" else "English", color = MaterialTheme.colorScheme.primary)
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Select", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text(text = if (language == "vi") "Chọn ngôn ngữ" else "Select language") },
                    text = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = language == "vi",
                                    onClick = {
                                        languageViewModel.setLanguage("vi")
                                        showLanguageDialog = false
                                        navController.navigate("home_screen/$userId") {
                                            popUpTo("settings_screen/$userId") { inclusive = true }
                                        }
                                    }
                                )
                                Text("Tiếng Việt")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = language == "en",
                                    onClick = {
                                        languageViewModel.setLanguage("en")
                                        showLanguageDialog = false
                                        navController.navigate("home_screen/$userId") {
                                            popUpTo("settings_screen/$userId") { inclusive = true }
                                        }
                                    }
                                )
                                Text("English")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLanguageDialog = false }) {
                            Text(text = if (language == "vi") "Hủy" else "Cancel")
                        }
                    }
                )
            }


            // Notifications Section
            Text(
                text = if (language == "vi") "Thông báo" else "Notification",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(2.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // Nền trắng
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "vi") "Nhận thông báo" else "Receive notifications",
                        fontSize = 16.sp
                    )

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = Color.Green,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Delete Account Button
            Button(
                onClick = { showDeleteAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = if (language == "vi") "Xóa tài khoản" else "Delete account",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = if (language == "vi") "Đăng xuất" else "Log out",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = if (language == "vi") "Đăng xuất" else "Log out")
            },
            text = {
                Text(text = if (language == "vi") "Bạn có chắc chắn muốn đăng xuất không?" else "Are you sure you want to log out?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Navigate back to login screen
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                ) {
                    Text(text = if (language == "vi") "Đăng xuất" else "Log out")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text(text = if (language == "vi") "Hủy" else "Cancel")
                }
            }
        )
    }


    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(text = if (language == "vi") "Xóa tài khoản" else "Delete Account")
            },
            text = {
                Text(
                    text = if (language == "vi")
                        "Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác và tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn."
                    else
                        "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently deleted."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            userRepository.deleteUser(userId)
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = if (language == "vi") "Xóa tài khoản" else "Delete Account")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(text = if (language == "vi") "Hủy" else "Cancel")
                }
            }
        )
    }
}