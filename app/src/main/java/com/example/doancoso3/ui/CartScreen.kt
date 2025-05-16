package com.example.doancoso3.ui

import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doancoso3.R
import android.graphics.BitmapFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import com.example.doancoso3.data.CartItem
import com.example.doancoso3.viewmodel.CartViewModel
import com.example.doancoso3.viewmodel.LanguageViewModel

@Composable
fun CartScreen(cartViewModel: CartViewModel, navController: NavController, userId: String, languageViewModel: LanguageViewModel) {
    val language by languageViewModel.language.collectAsState()
    val items = cartViewModel.cartItems
    val totalPrice = items.sumOf { it.quantity * it.product.GiaTien }
    val isLoading by cartViewModel.isLoading

    LaunchedEffect(userId) {
        cartViewModel.loadCartItems(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = if (language == "vi") "Giỏ hàng" else "Cart",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(18.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (items.isEmpty()) {
                Text(
                    text = if (language == "vi") "Giỏ hàng đang trống" else "Your cart is empty",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items) { item ->
                        CartItemBox(item = item, cartViewModel = cartViewModel, userId = userId)
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (language == "vi") "Tổng: ${formatCurrency(totalPrice.toInt())}" else "Total: ${formatCurrency(totalPrice.toInt())}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // giữ chỗ cho nút thanh toán
        }

        // Nút thanh toán luôn ở dưới cùng
        Button(
            onClick = { navController.navigate("checkoutScreen/$userId") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text(
                text = if (language == "vi") "Thanh toán" else "Checkout",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CartItemBox(item: CartItem, cartViewModel: CartViewModel, userId: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ShowImageFromAssets(
                imageName = item.product.HinhAnh,
                modifier = Modifier.weight(0.4f)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(0.6f)) {
                Text(text = item.product.TenSP, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = " ${formatCurrency(item.product.GiaTien.toInt())}", fontSize = 14.sp, color = Color.Gray)


                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { cartViewModel.decreaseQuantity(userId, item) }) {
                        Text(text = "-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(text = item.quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    IconButton(onClick = { cartViewModel.increaseQuantity(userId, item) }) {
                        Text(text = "+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = {
                cartViewModel.removeFromCart(userId, item.product.ID)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}



@Composable
fun ShowImageFromAssets(imageName: String, modifier: Modifier) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeStream(context.assets.open("anh-man-hinh/$imageName"))?.asImageBitmap()

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image from Assets",
            modifier = modifier
                .size(80.dp) // Kích thước hình ảnh nhỏ hơn (1/5 box)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

