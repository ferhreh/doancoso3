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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.doancoso3.R
import com.example.doancoso3.model.Product
import com.example.doancoso3.data.ProductFirestoreRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.grid.items
import com.example.doancoso3.viewmodel.LanguageViewModel

data class Filter(
    val title: Map<String, String>, // Thay vì String, dùng Map để chứa các ngôn ngữ
    val iconRes: Int
)
val filters = listOf(
    Filter(mapOf("vi" to "Phổ biến", "en" to "Popular"), R.drawable.favorite),
    Filter(mapOf("vi" to "Con chuột", "en" to "Mouse"), R.drawable.left_click),
    Filter(mapOf("vi" to "Bàn phím", "en" to "Keyboard"), R.drawable.keyboard),
    Filter(mapOf("vi" to "Màn hình", "en" to "Monitor"), R.drawable.computer_screen),
    Filter(mapOf("vi" to "Tai nghe", "en" to "Headphones"), R.drawable.headphones)
)
@Composable
fun HomeScreen(navController: NavHostController, userId: String, languageViewModel: LanguageViewModel) {
    var selectedFilter by remember { mutableStateOf(filters[0]) }
    var selectedNavItem by remember { mutableStateOf(0) }
    val language by languageViewModel.language.collectAsState()
    val products = remember { mutableStateListOf<Product>() }
    val filteredProducts = remember { mutableStateListOf<Product>() }
    val repo = remember { ProductFirestoreRepository() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedProducts = repo.getProducts()
            products.clear()
            products.addAll(fetchedProducts)
            filteredProducts.clear()
            filteredProducts.addAll(fetchedProducts)
        }
    }

    fun updateFilteredProducts(filter: Filter, language: String) {
        val categoryName = filter.title[language] ?: filter.title["vi"] ?: ""
        filteredProducts.clear()
        filteredProducts.addAll(products.filter { it.DanhMuc == categoryName })
    }

    Scaffold(
        bottomBar = {
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

                    if (navController.currentDestination?.route != route) {
                        navController.navigate(route) {
                            popUpTo("home_screen/$userId") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                navController = navController,
                userId = userId,
                language = language
            )
        }
    ) {  innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("searchScreen/$userId")}) {
                    Image(painter = painterResource(id = R.drawable.search_icon), contentDescription = "Search", modifier = Modifier.size(34.dp).padding(start = 16.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Make home", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp), color = Color.LightGray)
                    Text(text = "BEAUTIFUL", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp), color = Color.Black)
                }
                IconButton(onClick = { navController.navigate("cartScreen/$userId")}) {
                    Image(painter = painterResource(id = R.drawable.cart_icon), contentDescription = "Shopping Cart", modifier = Modifier.padding(end = 16.dp).size(28.dp))
                }
            }

            FilterToolbar(filters = filters, onFilterSelected = {
                selectedFilter = it
                updateFilteredProducts(it, language)
            }, language = language)

            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), modifier = Modifier.padding(8.dp)) {
                items(filteredProducts) { item ->
                    HomeItem(item, navController, userId)
                }
            }
        }
    }
}

@Composable
    fun HomeItem(item: Product, navController: NavHostController, userId: String) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .width(340.dp)
            .height(260.dp)
            .clickable {
                navController.navigate("productDetail/${item.ID}/$userId")
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
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(10.dp))
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
                    text = formatCurrency(item.GiaTien.toInt()),
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

fun formatCurrency(amount: Double): String {
    val formattedAmount = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
    return formattedAmount.replace("₫", " VND")
}

@Composable
fun FilterToolbar(filters: List<Filter>, onFilterSelected: (Filter) -> Unit, language: String = "vi") {
    LazyRow(
        modifier = Modifier.padding(bottom = 0.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(filter = filter, onClick = { onFilterSelected(filter) }, language = language)
        }
    }
}

@Composable
fun FilterChip(filter: Filter, onClick: () -> Unit, language: String = "vi") {
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
                painter = painterResource(id = filter.iconRes),
                contentDescription = filter.title[language],
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = filter.title[language] ?: filter.title["vi"].orEmpty(),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavController,
    userId: String,
    language: String = "vi"
) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        val items = listOf(
            BottomNavItem(if (language == "vi") "Trang chủ" else "Home", Icons.Filled.Home, "home_screen/$userId"),
            BottomNavItem(if (language == "vi") "Yêu thích" else "Favorites", Icons.Filled.Favorite, "favorite_screen/$userId"),
            BottomNavItem(if (language == "vi") "Thông báo" else "Notify", Icons.Filled.Notifications, "notification_screen/$userId"),
            BottomNavItem(if (language == "vi") "Cá nhân" else "Profile", Icons.Filled.Person, "profile_screen/$userId")
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
                    navController.navigate(item.route)
                }
            )
        }
    }
}
