package com.example.doancoso3.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso3.R
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale


data class Filter(val name: String, val imageId: Int)

val filters = listOf(
    Filter("Phổ biến", R.drawable.favorite),
    Filter("Chuột", R.drawable.left_click),
    Filter("Bàn phím", R.drawable.keyboard),
    Filter("Màn hình", R.drawable.computer_screen),
    Filter("Tai nghe", R.drawable.headphones)
)

@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedFilter by remember { mutableStateOf(filters[0]) }
    var selectedNavItem by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val copyDbHelper = CopyDbHelper(context)

    val products = remember { mutableStateListOf<Product>() }
    val filteredProducts = remember { mutableStateListOf<Product>() }

    LaunchedEffect(Unit) {
        val productDb = copyDbHelper.getProductDb()
        products.addAll(productDb.getProducts())
        filteredProducts.addAll(products)
    }

    fun updateFilteredProducts(filter: Filter) {
        filteredProducts.clear()
        filteredProducts.addAll(products.filter { it.DanhMuc == filter.name })
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedNavItem,
                onItemSelected = { index ->
                    selectedNavItem = index

                    // Điều hướng đến màn hình tương ứng
                    when (index) {
                        0 -> navController.navigate("home_screen")
                        1 -> navController.navigate("favorite_screen") // Điều hướng đến FavoriteScreen
                        2 -> navController.navigate("notification_screen")
                        3 -> navController.navigate("profile_screen")
                    }
                },
                navController = navController // Truyền NavController vào
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Biểu tượng tìm kiếm
                IconButton(onClick = { /* Tìm kiếm */ }) {
                    Image(painter = painterResource(id = R.drawable.search_icon), contentDescription = "Search", modifier = Modifier.size(34.dp).padding(start = 16.dp))
                }
                // Tiêu đề
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Make home", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp), color = Color.LightGray)
                    Text(text = "BEAUTIFUL", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp), color = Color.Black)
                }
                // Biểu tượng giỏ hàng
                IconButton(onClick = { /* Giỏ hàng */ }) {
                    Image(painter = painterResource(id = R.drawable.cart_icon), contentDescription = "Shopping Cart", modifier = Modifier.padding(end = 16.dp).size(28.dp))
                }
            }

            // Thanh công cụ bộ lọc
            FilterToolbar(filters) { filter ->
                selectedFilter = filter
                updateFilteredProducts(filter)
            }

            // Hiển thị sản phẩm
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(8.dp)) {
                items(filteredProducts) { item ->
                    HomeItem(item, navController) // Truyền NavController vào HomeItem
                }
            }
        }
    }
}

@Composable
fun HomeItem(item: Product, navController: NavHostController) {
    Box(
        modifier = Modifier
            .padding(top=0.dp)
            .padding(end= 8.dp)
            .padding(start = 8.dp)
            .padding(bottom = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .width(340.dp)
            .height(260.dp)
            .clickable {
                navController.navigate("productDetail/${item.ID}") // Điều hướng đến ProductDetailScreen
            }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.White),
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ShowImageFromAssetss(item.HinhAnh)

                    // Icon giỏ hàng nằm ở góc dưới bên phải
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Gray,  shape = RoundedCornerShape(10.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.shopping_bag),
                            contentDescription = "Shopping Bag",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = item.TenSP,
                    style = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.Gray),
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = formatCurrency(item.GiaTien),
                    style = LocalTextStyle.current.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
        }
    }
}
@Composable
fun ShowImageFromAssetss(imageName: String) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeStream(context.assets.open("anh-man-hinh/$imageName"))?.asImageBitmap()

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Image from Assets",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
// Hàm định dạng giá tiền
fun formatCurrency(amount: Double): String {
    val formattedAmount = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
    return formattedAmount.replace("₫", " VND") // Thay đổi ký hiệu tiền tệ nếu cần
}
@Composable
fun FilterToolbar(filters: List<Filter>, onFilterSelected: (Filter) -> Unit) {
    LazyRow(
        modifier = Modifier
            .padding(bottom = 0.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(filter = filter, onClick = { onFilterSelected(filter) })
        }
    }
}

@Composable
fun FilterChip(filter: Filter, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = filter.imageId),
                contentDescription = filter.name,
                modifier = Modifier.size(40.dp)
            )
            Text(text = filter.name, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

    @Composable
    fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit,navController: NavController) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color.Black
        ) {
            val items = listOf(
                BottomNavItem("Trang chủ", Icons.Filled.Home, "home_screen"),
                BottomNavItem("Yêu thích", Icons.Filled.Favorite, "favorite_screen"),
                BottomNavItem("Thông báo", Icons.Filled.Notifications, "notification_screen"),
                BottomNavItem("Cá nhân", Icons.Filled.Person, "profile_screen")
            )

            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 14.sp
                        )
                    },
                    selected = selectedIndex == index,
                    onClick = {
                        onItemSelected(index)
                        navController.navigate(item.route) // Điều hướng đến màn hình tương ứng
                    }
                )
            }
        }
    }