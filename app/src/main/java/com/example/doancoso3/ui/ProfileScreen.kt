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
import com.example.doancoso3.data.UserDb
import com.example.doancoso3.model.User

@Composable
fun ProfileScreen(navController: NavController, userDb: UserDb, userId: Int) {
    val user = remember { mutableStateOf<User?>(null) }
    var selectedNavItem by remember { mutableStateOf(3) } // Chọn mục Profile mặc định

    // Lấy thông tin người dùng
    LaunchedEffect(userId) {
        val cursor = userDb.getUserDataById(userId)
        if (cursor.moveToFirst()) {
            user.value = User(
                id = userId,
                UserName = cursor.getString(0),
                Email = cursor.getString(1),
                Password = ""
            )
        }
        cursor.close()
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
                userId = userId
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
                Text(text = "Cá nhân", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                            text = user.value?.UserName ?: "Đang tải...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.value?.Email ?: "",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Menu tùy chọn của người dùng
            ProfileMenu(navController, userId)
        }
    }
}

@Composable
fun ProfileMenu(navController: NavController, userId: Int) {
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
                                text = "Đơn hàng của tôi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Đã có 10 đơn hàng",
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
                    .clickable { navController.navigate("reviews/$userId") }
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
                                text = "Đánh giá của tôi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Đánh giá cho 5 sản phẩm",
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
                    .clickable { navController.navigate("settings/$userId") }
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
                                text = "Cài đặt",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Thông báo, Mật khẩu, FAQ, Liên hệ",
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
                text = "Cảm ơn bạn đã sử dụng ứng dụng!",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}