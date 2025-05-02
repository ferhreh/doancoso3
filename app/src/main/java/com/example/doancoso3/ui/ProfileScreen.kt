package com.example.doancoso3.ui
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.R
import com.example.doancoso3.data.UserFirestoreRepository
import com.example.doancoso3.model.User
import com.example.doancoso3.viewmodel.LanguageViewModel

@Composable
fun ProfileScreen(navController: NavController, userId: String,userRepo: UserFirestoreRepository,languageViewModel: LanguageViewModel) {
    val language by languageViewModel.language.collectAsState()
    val user = remember { mutableStateOf<User?>(null) }
    var selectedNavItem by remember { mutableStateOf(3) }

    LaunchedEffect(userId) {
        user.value = userRepo.getUserById(userId)
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedNavItem,
                onItemSelected = { index ->
                    selectedNavItem = index
                    val route = when (index) {
                        0 -> "home_screen/$userId"
                        1 -> "favorite_screen/$userId"
                        2 -> "notification_screen/$userId"
                        3 -> "profile_screen/$userId"
                        else -> "home_screen/$userId"
                    }
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navController = navController,
                userId = userId,
                language = language
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Thanh tiêu đề
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (language == "en") "Profile" else "Cá nhân",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { /* Xử lý đăng xuất */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Hiển thị thông tin người dùng
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "User Avatar",
                        modifier = Modifier.size(64.dp)
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = user.value?.name ?: "Đang tải...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.value?.email ?: "",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Menu tùy chọn của người dùng
            ProfileMenu(navController, userId, language)
        }
    }
}

@Composable
fun ProfileMenu(navController: NavController, userId: String, language: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            // Menu Item 1: Đơn hàng của tôi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { navController.navigate("orders/$userId") }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "en") "My Orders" else "Đơn hàng của tôi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (language == "en") "You have 10 orders" else "Đã có 10 đơn hàng",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
            // Menu Item 4: Đánh giá của tôi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { navController.navigate("my_feedback_screen/$userId") }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "en") "My Reviews" else "Đánh giá của tôi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (language == "en") "Reviews for 5 products" else "Đánh giá cho 5 sản phẩm",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            // Menu Item 5: Cài đặt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {navController.navigate("settings_screen/$userId") }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == "en") "Settings" else "Cài đặt",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (language == "en")
                                    "Notifications, Password, FAQ, Contact"
                                else
                                    "Thông báo, Mật khẩu, FAQ, Liên hệ",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            // Thêm khoảng cách dưới cùng nếu cần
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (language == "en") "Thank you for using the app!" else "Cảm ơn bạn đã sử dụng ứng dụng!",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}