package com.example.doancoso3.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.example.doancoso3.ui.theme.Doancoso3Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.CartViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doancoso3.data.UserAddressDb
import com.example.doancoso3.model.UserAddress
import com.example.doancoso3.viewmodel.AddressViewModel
import com.example.doancoso3.viewmodel.CartViewModelFactory
import com.example.doancoso3.viewmodel.FavoritesViewModel
import com.example.doancoso3.viewmodel.FavoritesViewModelFactory

class MainActivity : ComponentActivity() {
    private var userId: Int = 0 // Giá trị mặc định
    private val dbHelper: CopyDbHelper by lazy { CopyDbHelper(this) }
    private val cartViewModel: CartViewModel by viewModels { CartViewModelFactory(dbHelper) }
    private lateinit var favoritesViewModel: FavoritesViewModel
    private val products = mutableListOf<Product>()
    private var db: CopyDbHelper? = null
    private lateinit var userAddressDb: UserAddressDb
    private val addressViewModel: AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        userId = intent?.getIntExtra("USER_ID", 0) ?: 0
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dbHelper.openDatabase() // Đảm bảo cơ sở dữ liệu đã mở trước khi sử dụng
        userAddressDb = dbHelper.userAddressDb()
        // Khởi tạo FavoritesViewModel
        val factory = FavoritesViewModelFactory(dbHelper, userId)
        favoritesViewModel = ViewModelProvider(this, factory).get(FavoritesViewModel::class.java)

        setContent {
            Doancoso3Theme {
                AppContent()
            }
        }
        db = dbHelper
    }

    @Composable
    fun AppContent() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                HomeScreen(navController, userId)
            }
            composable("productDetail/{productId}/{userId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0

                val product = productId?.let { findProductById(it.toString()) }

                if (product != null) {
                    ProductDetailScreen(userId, product, cartViewModel, favoritesViewModel, navController)
                } else {
                    navController.navigate("home_screen/$userId") {
                        popUpTo("home_screen/$userId") { inclusive = true }
                    }
                }
            }
            composable("cartScreen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                CartScreen(cartViewModel, navController, userId)
            }
            composable("favorite_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                FavoriteScreen(userId, favoritesViewModel, cartViewModel, navController)
            }
            composable("checkoutScreen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                CheckoutScreen(navController, cartViewModel, userId, addressViewModel)
            }
            composable("saved_addresses/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                SavedAddressesScreen(navController, userAddressDb, userId, addressViewModel)
            }
            composable("editAddressScreen/{addressId}") { backStackEntry ->
                val addressId = backStackEntry.arguments?.getString("addressId")?.toIntOrNull() ?: 0
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                EditAddressScreen(
                    navController,
                    userAddressDb,
                    userId,
                    addressId = addressId
                )
            }
            composable("add_address/{userId}") {  backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                AddAddressScreen(navController, userAddressDb, userId)
            }

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AppContentPreview() {
        AppContent()
    }

    private fun findProductById(productId: String?): Product? {
        val idInt = productId?.toIntOrNull() ?: return null
        return products.find { it.ID == idInt } ?: db?.getProductById(idInt)
    }

}
