package com.example.doancoso3.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso3.R
import com.example.doancoso3.data.UserFirestoreRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userRepo = remember { UserFirestoreRepository() }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            color = Color(192, 192, 192),
            text = "XIN CHÀO..!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "CHÀO MỪNG BẠN ĐẾN VỚI CHÚNG TÔI",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 22.sp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Email", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nhập Email") },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Password", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập Mật Khẩu") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) R.drawable.view else R.drawable.hide
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val isValid = userRepo.checkUser(email, password)
                            if (isValid) {
                                val userId: String? = userRepo.getUserId(email, password)
                                if (userId != null && userId.toString().isNotEmpty()) {
                                    navController.navigate("home_screen/${userId}")
                                } else {
                                    Toast.makeText(context, "ID người dùng không hợp lệ!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
            ) {
                Text(text = "Đăng nhập", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ĐĂNG KÝ",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily(Font(R.font.roboto_regular))
            ),
            color = Color.Black,
            fontSize = 26.sp,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .wrapContentWidth()
                .clickable { navController.navigate("signup") }
        )
    }
}
