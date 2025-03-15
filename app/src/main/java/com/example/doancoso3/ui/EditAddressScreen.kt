package com.example.doancoso3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.doancoso3.data.UserAddressDb
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect

@Composable
fun EditAddressScreen(
    navController: NavController,
    userAddressDb: UserAddressDb,
    userId: Int,
    addressId: Int
) {
    // Lấy dữ liệu địa chỉ từ database
    val addressList by remember { mutableStateOf(userAddressDb.getUserAddresses(userId)) }
    val address = addressList.find { it.id == addressId }

    // Nếu không tìm thấy địa chỉ, quay lại màn hình trước
    LaunchedEffect(address) {
        if (address == null) {
            navController.popBackStack()
        }
    }

    // State cho TextField
    var name by remember { mutableStateOf(address?.name ?: "") }
    var addressText by remember { mutableStateOf(address?.address ?: "") }
    var phone by remember { mutableStateOf(address?.phoneNumber ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Tiêu đề
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Sửa địa chỉ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Họ và Tên", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Địa Chỉ", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = addressText,
                onValueChange = { addressText = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Số Điện Thoại", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Button cập nhật
        Button(
            onClick = {
                userAddressDb.updateUserAddress(addressId, name, addressText, phone)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text(
                "Cập nhật địa chỉ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
