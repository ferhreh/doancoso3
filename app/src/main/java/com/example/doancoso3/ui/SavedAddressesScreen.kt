package com.example.doancoso3.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.doancoso3.R
import com.example.doancoso3.viewmodel.AddressViewModel

@Composable
fun SavedAddressesScreen(
    navController: NavController,
    userId: String,
    addressViewModel: AddressViewModel
) {
    val addresses by addressViewModel.addresses.collectAsState()
    val selectedAddress by addressViewModel.selectedAddress.collectAsState()

    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Tiêu đề
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Chọn địa chỉ", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (addresses.isEmpty()) {
                Text(text = "Không có địa chỉ nào được lưu", color = Color.Gray)
            } else {
                LazyColumn {
                    items(addresses) { address ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Phần thông tin địa chỉ
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { addressViewModel.setSelectedAddress(address) }
                            ) {
                                Text(text = "Tên: ${address.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Địa chỉ: ${address.address}", fontSize = 14.sp)
                                Text(text = "Số điện thoại: ${address.phone}", fontSize = 14.sp)
                            }

                            // Nút sửa địa chỉ
                            IconButton(
                                onClick = {
                                    // Chuyển đến trang EditAddressScreen với thông tin địa chỉ
                                    navController.navigate("editAddressScreen/${address.id}")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit_text),
                                    contentDescription = "Sửa địa chỉ",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Checkbox chọn địa chỉ
                            Checkbox(
                                checked = (selectedAddress == address),
                                onCheckedChange = { addressViewModel.setSelectedAddress(address) }
                            )
                        }

                        // Thêm đường gạch giữa các địa chỉ (không thêm sau địa chỉ cuối cùng)
                        if (address != addresses.last()) {
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }
                }
            }
        }

        // Button ở dưới cùng styled giống như button lưu địa chỉ
        Button(
            onClick = {
                selectedAddress?.let { address ->
                    addressViewModel.setSelectedAddress(address)
                    navController.navigate("checkoutScreen/$userId")
                }
            },
            enabled = selectedAddress != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                disabledBackgroundColor = Color.Gray
            )
        ) {
            Text(
                text = "Xác nhận",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}