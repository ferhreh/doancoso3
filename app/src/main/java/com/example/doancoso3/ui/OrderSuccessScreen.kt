package com.example.doancoso3.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.viewmodel.LanguageViewModel

@Composable
fun OrderSuccessScreen(navController: NavController, userId: String, languageViewModel: LanguageViewModel) {
    val language by languageViewModel.language.collectAsState()
    val greenColor = Color(0xFF4CAF50)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon thành công
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(greenColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Tiêu đề
            Text(
                text = if (language == "en") "Order Placed Successfully!" else "Đặt hàng thành công!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mô tả
            Text(
                text = if (language == "en")
                    "Thank you for your purchase. Your order has been placed successfully and is being processed."
                else
                    "Cảm ơn bạn đã mua hàng. Đơn hàng của bạn đã được đặt thành công và đang được xử lý.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nút tiếp tục mua hàng
            Button(
                onClick = { navController.navigate("home_screen/$userId") {
                    popUpTo("home_screen/$userId") { inclusive = true }
                } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
            ) {
                Text(
                    text = if (language == "en") "Continue Shopping" else "Tiếp tục mua hàng",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nút xem đơn hàng
            OutlinedButton(
                onClick = { navController.navigate("orders/$userId") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.White)
            ) {
                Text(
                    text = if (language == "en") "View Your Orders" else "Xem đơn hàng của bạn",
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}