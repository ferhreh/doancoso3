package com.example.doancoso3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.doancoso3.data.UserAddressFirestoreRepository
import com.example.doancoso3.viewmodel.LanguageViewModel


@Composable
fun AddAddressScreen(
    navController: NavController,
    userId: String,
    repository: UserAddressFirestoreRepository,
    languageViewModel: LanguageViewModel
) {
    val language by languageViewModel.language.collectAsState()
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var shouldSave by remember { mutableStateOf(false) }

    // Đây là nơi dùng LaunchedEffect đúng cách
    if (shouldSave) {
        LaunchedEffect(key1 = true) {
            isSaving = true
            repository.addUserAddress(userId, name, address, phone)
            navController.popBackStack()
            shouldSave = false
            isSaving = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (language == "en") "Add Address" else "Thêm địa chỉ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (language == "en") "Full Name" else "Họ và Tên") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = {  Text(if (language == "en") "Address" else "Địa Chỉ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (language == "en") "Phone Number" else "Số Điện Thoại") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                shouldSave = true
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text(
                text = if (language == "en") "Save Address" else "Lưu Địa Chỉ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
