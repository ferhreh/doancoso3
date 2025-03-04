package com.example.doancoso3.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.doancoso3.data.CopyDbHelper
import androidx.compose.runtime.*

@Composable
fun SignUpScreen(navController: NavHostController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dbHelper = CopyDbHelper(context)
    val userDb = remember { dbHelper.getUserDb(context) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            color = Color(192, 192, 192),
            text = "BẮT ĐẦU THÔI!!",
            style =LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Đăng ký miễn phí",
            style = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name input
        Text(text = "Tên", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nhập Tên") },
            shape = RoundedCornerShape(0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email input
        Text(text = "Email", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nhập Email") },
            shape = RoundedCornerShape(0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input
        Text(text = "Mật Khẩu", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập Mật Khẩu") },
            shape = RoundedCornerShape(0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password input
        Text(text = "Xác Nhận Mật Khẩu", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Nhập Xác Nhận Mật Khẩu") },
            shape = RoundedCornerShape(0.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        userDb.addUser(name, email, password)
                        Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") // Chuyển đến trang đăng nhập
                    } else {
                        Toast.makeText(context, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(250.dp)
                    .height(56.dp)
            ) {
                Text(text = "Đăng ký", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Link to sign in
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bạn đã có tài khoản? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 14.sp
            )
            Text(
                text = "ĐĂNG NHẬP",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable { navController.navigate("login") }
            )
        }
    }
}