package com.example.doancoso3.ui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import com.example.doancoso3.R
import com.example.doancoso3.data.FeedbackFirestoreRepository
import com.example.doancoso3.data.OrderFirestoreRepository
import com.example.doancoso3.data.ProductFirestoreRepository
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.FavoritesViewModel
import com.example.doancoso3.viewmodel.LanguageViewModel
import java.text.NumberFormat
import java.util.Locale



@SuppressLint("DefaultLocale")
@Composable
fun ProductDetailScreen(
    userId: String,
    productId: String,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController,
    languageViewModel: LanguageViewModel
) {
    val language by languageViewModel.language.collectAsState()
    val context = LocalContext.current
    val repository = remember { ProductFirestoreRepository() }
    val orderRepository = remember { OrderFirestoreRepository() }
    val feedbackRepository = remember { FeedbackFirestoreRepository() }

    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var soldCount by remember { mutableStateOf(0) }
    var averageRating by remember { mutableStateOf(0f) }
    var feedbackCount by remember { mutableStateOf(0) }

    // Fetch product and related data
    LaunchedEffect(productId) {
        try {
            // Get product details
            val productResult = repository.getProductById(productId)
            product = productResult

            // Count sold items from orders
            val allOrders = orderRepository.getAllOrders()
            soldCount = allOrders
                .filter { it.productId == productId && it.status >= 2 } // Only count completed orders
                .sumOf { it.soLuong }

            // Get average rating and feedback count
            val feedbacks = feedbackRepository.getFeedbacksByProductId(productId)
            averageRating = if (feedbacks.isNotEmpty()) {
                feedbacks.sumOf { it.rating.toDouble() }.toFloat() / feedbacks.size
            } else {
                0f
            }
            feedbackCount = feedbacks.size
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    // UI states
    val quantity = remember { mutableStateOf(1) }
    val selectedColor = remember { mutableStateOf(Color(0xFF222222)) }

    val selectedImage = when (selectedColor.value) {
        Color(0xFF222222) -> product?.HinhAnh1.takeIf { !it.isNullOrBlank() }
        Color(0xFFFFFFFF) -> product?.HinhAnh2.takeIf { !it.isNullOrBlank() }
        Color(0xFFFF99CC) -> product?.HinhAnh3.takeIf { !it.isNullOrBlank() }
        else -> null
    }

    val totalPrice by remember { derivedStateOf { (product?.GiaTien ?: 0) * quantity.value } }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (language == "vi") "Không tìm thấy sản phẩm" else "Product not found",
                color = Color.Gray
            )
        }
    } else {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Image + Color Picker
            Row(
                modifier = Modifier
                    .height(screenHeight * 0.55f)
                    .fillMaxWidth()
            ) {
                // Left Column
                Column(
                    modifier = Modifier
                        .padding(top = 26.dp)
                        .width(60.dp)
                        .fillMaxHeight()
                        .offset(x = 30.dp)
                        .zIndex(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .shadow(10.dp, RoundedCornerShape(30.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .shadow(10.dp, RoundedCornerShape(30.dp))
                            .clip(RoundedCornerShape(30.dp))
                            .height(150.dp)
                            .background(Color.White)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(
                                Color(0xFF222222),
                                Color(0xFFFFFFFF),
                                Color(0xFFFF99CC)
                            ).forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (selectedColor.value == color) 2.dp else 0.dp,
                                            color = Color.Black,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor.value = color }
                                )
                            }
                        }
                    }
                }

                // Image Viewer
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f)
                        .zIndex(0f)
                ) {
                    if (selectedImage != null) {
                        ShowImageFromAssets(selectedImage, contentScale = ContentScale.FillBounds)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == "vi") "Hiện chưa có sản phẩm màu bạn chọn" else "No product available in this color",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product!!.TenSP,
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
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = formatCurrency(totalPrice.toInt()),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { if (quantity.value > 1) quantity.value-- },
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(quantity.value.toString(), fontSize = 20.sp)

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = { quantity.value++ },
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add sold count
            Text(
                text = if (language == "vi") "Đã bán: $soldCount" else "Sold: $soldCount",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add rating card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ), // Thêm viền
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%.1f", averageRating),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.star_on),
                            contentDescription = "Star",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "($feedbackCount)",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navController.navigate("feedback_product_screen/$productId")
                        }
                    ) {
                        Text(
                            text = if (language == "vi") "Tất cả" else "All",
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View All",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val moTaFormatted = product!!.MoTa.replace(" -", "\n-")

            Text(
                text = moTaFormatted,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        favoritesViewModel.addToFavorites(product!!, userId)
                        Toast.makeText(
                            context,
                            if (language == "vi") "Đã thêm vào danh sách yêu thích" else "Added to favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFEEEEEE), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        cartViewModel.addToCart(userId, product!!, quantity.value)
                        navController.navigate("cartScreen/$userId")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = if (language == "vi") "Thêm vào giỏ hàng" else "Add to Cart",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun formatCurrency(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}
@Composable
fun ColorOption(color: Color, selectedColor: MutableState<Color>) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selectedColor.value == color) 3.dp else 1.dp,
                color = if (selectedColor.value == color) Color.Black else Color.Gray,
                shape = CircleShape
            )
            .clickable { selectedColor.value = color }
    )
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




