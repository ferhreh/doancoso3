package com.example.doancoso3.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.R
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.viewmodel.CartViewModel

@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel, userId: Int) {
    val cartItems = cartViewModel.cartItems
    val shippingFee = 5000.0 // Phí ship cố định
    val totalPrice = cartItems.sumOf { it.product.GiaTien * it.quantity } + shippingFee

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Nội dung cuộn (trừ phần tổng tiền)
        Column(
            modifier = Modifier
                .weight(1f) // ✅ Nội dung phía trên có thể cuộn
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Tiêu đề
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = "Thanh toán", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Địa chỉ nhận hàng
            DeliveryAddressSection()

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Danh sách sản phẩm của bạn",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Danh sách sản phẩm cuộn được
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 400.dp)
                    .verticalScroll(rememberScrollState()) // ✅ Thanh cuộn khi nhiều sản phẩm
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            ) {
                Column {
                    cartItems.forEach { cartItem ->
                        CheckoutItem(cartItem, cartViewModel,userId)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            PaymentAndDeliverySection()
        }

        // ✅ Tổng tiền + Nút đặt hàng luôn cố định dưới màn hình
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Đặt nền trắng
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Đảm bảo màu nền trắng
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tổng tiền hàng:", fontSize = 16.sp)
                    Text(text = formatCurrency(cartItems.sumOf { it.product.GiaTien * it.quantity }), fontSize = 16.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tổng tiền phí vận chuyển:", fontSize = 16.sp)
                    Text(text = formatCurrency(shippingFee), fontSize = 16.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tổng tiền thanh toán:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = formatCurrency(totalPrice), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                // Nút đặt hàng
                Button(
                    onClick = { /* Xử lý thanh toán */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text(text = "Đặt hàng", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}


@Composable
fun DeliveryAddressSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom=0.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Hộp chứa địa chỉ
        Box(
            modifier = Modifier
                .weight(3f)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .shadow(4.dp, RoundedCornerShape(2.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Địa chỉ:", fontWeight = FontWeight.Bold)
                    Icon(
                        painter = painterResource(id = R.drawable.editing), // Icon chỉnh sửa
                        contentDescription = "Edit Address",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "216 St Paul's Rd, London N1 2LL, UK", fontSize = 14.sp)
                Text(text = "Contact: +44-784232", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Hộp chứa icon thêm địa chỉ
        Box(
            modifier = Modifier
                .weight(0.8f)
                .size(80.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .shadow(4.dp, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add), // Icon thêm địa chỉ
                contentDescription = "Add Address",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
fun CheckoutItem(cartItem: CartItem, cartViewModel: CartViewModel, userId: Int) {
    Box(modifier = Modifier.fillMaxWidth().padding(6.dp).shadow(2.dp, RoundedCornerShape(2.dp)).background(Color.White) ) {
        Column{
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShowImageFromAssets(
                    imageName = cartItem.product.HinhAnh,
                    modifier = Modifier.weight(0.5f)
                )
                Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                    Text(text = cartItem.product.TenSP, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Màu sắc: Đỏ", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "${formatCurrency(cartItem.product.GiaTien)}", fontSize = 14.sp, color = Color.Gray)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {cartViewModel.decreaseQuantity(userId, cartItem) }) {
                            Text(text = "-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Text(text = cartItem.quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        IconButton(onClick = { cartViewModel.increaseQuantity(userId,cartItem) }) {
                            Text(text = "+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                    IconButton(
                        onClick = {cartViewModel.removeFromCart(cartItem.userId, cartItem.product.TenSP) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp)
                        )
                    }
            }
            Divider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.3f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng tiền:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatCurrency(cartItem.product.GiaTien * cartItem.quantity),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun PaymentAndDeliverySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Phương thức thanh toán
        PaymentMethodSection()

        Spacer(modifier = Modifier.height(8.dp))

        // Phương thức giao hàng
        DeliveryMethodSection()
    }
}

@Composable
fun PaymentMethodSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(2.dp, RoundedCornerShape(2.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Phương thức thanh toán", fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(id = R.drawable.edit_text),
                    contentDescription = "Edit Payment Method",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pay), // Hình thẻ
                        contentDescription = "thanh toán khi nhận",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Thanh toán khi nhận hàng", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DeliveryMethodSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(2.dp, RoundedCornerShape(2.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Phương thức giao hàng", fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(id = R.drawable.edit_text),
                    contentDescription = "Edit Delivery Method",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dhl_emblem), // Hình DHL
                        contentDescription = "Delivery Method",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Nhanh (2-3 ngày)", fontSize = 16.sp)
                }
            }
        }
    }
}



