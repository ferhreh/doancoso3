package com.example.doancoso3.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.doancoso3.R
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.CartViewModel
import com.example.doancoso3.viewmodel.FavoritesViewModel

@Composable
fun FavoriteScreen(
    userId: Int,
    favoritesViewModel: FavoritesViewModel,
    cartViewModel: CartViewModel,
    navController: NavController
) {

    val favoriteItems = favoritesViewModel.favorites.collectAsStateWithLifecycle()
    var selectedNavItem by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        // Thanh tiêu đề
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Chức năng tìm kiếm */ }) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search",
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "Yêu thích",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { navController.navigate("cartScreen/$userId") }) {
                Image(
                    painter = painterResource(id = R.drawable.cart_icon),
                    contentDescription = "Shopping Cart",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        // Danh sách sản phẩm yêu thích
        LazyColumn(modifier = Modifier.weight(1f)) {
            val favoriteList = favoriteItems.value

            itemsIndexed(favoriteList) { index, product ->
                FavoriteItemBox(
                    product = product,
                    onRemove = { favoritesViewModel.removeFromFavorites(product) },
                    onAddToCart = {
                        cartViewModel.addToCart(userId, product, 1)
                        favoritesViewModel.removeFromFavorites(product)
                    }
                )
            }
        }

        // Nút "Add all to my cart"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    favoriteItems.value.forEach { product ->
                        cartViewModel.addToCart(userId, product, 1) // Thêm userId
                        favoritesViewModel.removeFromFavorites(product)
                    }
                    navController.navigate("cartScreen/$userId")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
            ) {
                Text(text = "Thêm tất cả vào giỏ hàng ", color = Color.White)
            }
        }

        // Thanh điều hướng
        BottomNavigationBar(
            selectedIndex = selectedNavItem,
            onItemSelected = { index ->
                selectedNavItem = index
                val route = when (index) {
                    0 -> "home_screen/$userId"
                    1 -> "favorite_screen/$userId"
                    2 -> "notification_screen/$userId"
                    3 -> "profile_screen/$userId"
                    else -> "home_screen/$userId"
                }
                try {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                } catch (e: Exception) {
                    Log.e("NavigationError", "Error navigating to $route: ${e.message}")
                }
            },
            navController = navController,
            userId = userId
        )
    }
}


// Mục sản phẩm yêu thích
@Composable
fun FavoriteItemBox(
    product: Product,  // Không cần 'val' ở đây
    onRemove: () -> Unit,
    onAddToCart: () -> Unit
)  {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ShowImageFromAssets(
                imageName = product.HinhAnh,
                modifier = Modifier.weight(0.4f) // Hình ảnh chiếm 40% width
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(0.6f)) {
                Text(text = product.TenSP, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = formatCurrency(product.GiaTien), fontSize = 14.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onAddToCart) {
                    Image(
                        painter = painterResource(id = R.drawable.cart_icon),
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onRemove) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


