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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doancoso3.model.UserAddress
import com.example.doancoso3.viewmodel.AddressViewModel
import com.example.doancoso3.viewmodel.LanguageViewModel

@Composable
fun EditAddressScreen(
    navController: NavController,
    userId: String,
    addressId: String,
    addressViewModel: AddressViewModel = viewModel(),
    languageViewModel: LanguageViewModel
) {
    val addresses by addressViewModel.addresses.collectAsState()
    var currentAddress by remember { mutableStateOf<UserAddress?>(null) }
    val language by languageViewModel.language.collectAsState()
    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses(userId)
    }

    LaunchedEffect(addresses) {
        currentAddress = addresses.find { it.id == addressId }
        if (currentAddress == null) {
            navController.popBackStack()
        }
    }

    // State cho TextField
    var name by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Khi currentAddress thay đổi thì update UI state
    LaunchedEffect(currentAddress) {
        currentAddress?.let {
            name = it.name
            addressText = it.address
            phone = it.phone
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
                    text = if (language == "en") "Edit Address" else "Sửa địa chỉ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = if (language == "en") "Full Name" else "Họ và Tên", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if (language == "en") "Address" else "Địa Chỉ", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = addressText,
                onValueChange = { addressText = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if (language == "en") "Phone Number" else "Số Điện Thoại", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                currentAddress?.let {
                    val updated = it.copy(name = name, address = addressText, phone = phone)
                    addressViewModel.updateAddress(userId, updated)
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text(
                text = if (language == "en") "Update Address" else "Cập nhật địa chỉ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

