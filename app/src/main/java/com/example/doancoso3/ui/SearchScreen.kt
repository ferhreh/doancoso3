package com.example.doancoso3.ui


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.LanguageViewModel
import com.example.doancoso3.viewmodel.SearchViewModel
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(
    navController: NavHostController,
    userId: String,
    searchViewModel: SearchViewModel,
    languageViewModel: LanguageViewModel
) {

    // State for search query
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by searchViewModel.searchResults.collectAsState()
    val bestsellers by searchViewModel.bestsellers.collectAsState()
    val favorites by searchViewModel.favorites.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val language by languageViewModel.language.collectAsState()
    val suggestedKeyword by searchViewModel.suggestedKeyword.collectAsState()
    var hasSearched by remember { mutableStateOf(false) }
    // Load initial data
    LaunchedEffect(true) {
        launch {
            searchViewModel.loadBestsellers()
        }
        launch {
            searchViewModel.loadFavorites()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = if (language == "en") "Search" else "Tìm kiếm",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(18.dp))
        }
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearch = {
                coroutineScope.launch {
                    hasSearched = true
                    searchViewModel.searchProducts(searchQuery)
                }
            },
            language =language
        )

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Search Results Section (shown only when there are results)
            if (searchResults.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = if (language == "en") "Search results: $searchQuery" else "Kết quả tìm kiếm: $searchQuery"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProductGrid(products = searchResults, navController = navController, userId = userId)
                }
            }
            if (hasSearched && searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = if (language == "en")
                            "No results for \"$searchQuery\""
                        else
                            "Không tìm thấy kết quả cho \"$searchQuery\"",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    suggestedKeyword?.let { suggestion ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == "en")
                                "Try searching for \"$suggestion\" instead?"
                            else
                                "Bạn có muốn thử tìm với \"$suggestion\" không?",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier.clickable {
                                searchQuery = suggestion
                                coroutineScope.launch {
                                    hasSearched = true
                                    searchViewModel.searchProducts(suggestion)
                                }
                            }
                        )
                    }
                }
            }
            // Bestsellers Section
            if (bestsellers.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = if (language == "en") "Bestsellers" else "Sản phẩm bán chạy"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(bestsellers) { product ->
                            ProductRowItem(product = product, navController,userId)
                        }
                    }
                }
            }

            // Favorites Section
            if (favorites.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = if (language == "en") "Favorite products" else "Sản phẩm yêu thích"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(favorites) { product ->
                            ProductRowItem(product = product, navController,userId)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    language: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            placeholder = {
                Text(if (language == "en") "Search for products..." else "Tìm kiếm sản phẩm...")
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = onSearch,
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Tìm kiếm"
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ProductGrid(products: List<Product>, navController: NavHostController, userId: String) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(400.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductGridItem(product = product, navController = navController, userId = userId)
        }
    }
}

@Composable
fun ProductGridItem(product: Product, navController: NavHostController, userId: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(top=0.dp)
            .padding(end= 8.dp)
            .padding(start = 8.dp)
            .padding(bottom = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .width(150.dp)
            .height(230.dp)
            .clickable {
                navController.navigate("productDetail/${product.ID}/$userId") // Điều hướng đến ProductDetailScreen
            }
    ) {
        Column {
            // Product Image
            ShowImageFromAssetss(product.HinhAnh)

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.TenSP,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCurrency(product.GiaTien.toInt()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ProductRowItem(product: Product, navController: NavHostController, userId: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(top=0.dp)
            .padding(end= 8.dp)
            .padding(start = 8.dp)
            .padding(bottom = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .width(150.dp)
            .height(230.dp)
            .clickable {
                navController.navigate("productDetail/${product.ID}/$userId") // Điều hướng đến ProductDetailScreen
            }
    ) {
        Column {
            // Product Image
            ShowImageFromAssetss(product.HinhAnh)

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = product.TenSP,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatCurrency(product.GiaTien.toInt()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

