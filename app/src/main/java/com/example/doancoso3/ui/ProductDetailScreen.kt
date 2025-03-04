package com.example.doancoso3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.example.doancoso3.viewmodel.CartViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.doancoso3.R
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.FavoritesViewModel


@Composable
fun ProductDetailScreen( userId: Int,product: Product, cartViewModel: CartViewModel, favoritesViewModel: FavoritesViewModel, navController: NavController) {
    val quantity = remember { mutableStateOf(1) } // Sử dụng mutableStateOf thay cho mutableIntStateOf
    val totalPrice by remember { derivedStateOf { product.GiaTien * quantity.value } }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(screenHeight * 0.55f)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .width(60.dp)
                    .padding(start = 4.dp)
                    .fillMaxHeight()
                    .offset(x = 30.dp)
                    .zIndex(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(30.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(30.dp))
                        .clip(RoundedCornerShape(30.dp))
                        .height(150.dp)
                        .background(Color.White)
                        .padding(10.dp)
                        .padding(start = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ColorOptions()
                }
            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f)
                    .zIndex(0f)
            ) {
                if (product.HinhAnh1 != null) {
                    ShowImageFromAssets(product.HinhAnh1, contentScale = ContentScale.FillBounds)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = product.TenSP,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily(Font(R.font.roboto_regular))
            ),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = formatCurrency(totalPrice),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()
            ) {
                TextButton(
                    onClick = { if (quantity.value > 1) quantity.value-- }, // Cập nhật quantity.value
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Text(text = "-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = quantity.value.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { quantity.value++ }, // Cập nhật quantity.value
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Text(text = "+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "4.5 (50 reviews)",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = product.MoTa,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = {
                    favoritesViewModel.addToFavorites(product)
                    Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFEEEEEE), shape = RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    cartViewModel.addToCart(userId, product, quantity.value) // Thêm sản phẩm vào giỏ hàng
                    navController.navigate("cartScreen/$userId") // Chuyển đến trang CartScreen
                },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Thêm vào giỏ hàng", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShowImageFromAssets(imageName: String, contentScale: ContentScale) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeStream(context.assets.open("anh-man-hinh/$imageName"))?.asImageBitmap()

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image from Assets",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds
        )
    }

}

@Composable
fun ColorOptions() {
    Column(
        modifier = Modifier
            .fillMaxHeight() // Lấp đầy chiều cao của `Box` cha
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceEvenly, // Phân bố đều theo chiều dọc
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
    ) {
        ColorOption(Color.Red)
        ColorOption(Color.Blue)
        ColorOption(Color.Green)
    }
}


@Composable
fun ColorOption(color: Color) {
    Box(
        modifier = Modifier
            .size(30.dp) // Đặt kích thước lớn hơn nếu cần
            .background(color, shape = RoundedCornerShape(25.dp)) // Tăng độ tròn của góc
            .border(2.dp, Color.Gray, RoundedCornerShape(25.dp)) // Tăng độ tròn của đường viền
            .clickable { /* Handle color selection */ }
    )
}