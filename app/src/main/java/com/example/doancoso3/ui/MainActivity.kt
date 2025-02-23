package com.example.doancoso3.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.doancoso3.ui.theme.Doancoso3Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doancoso3.data.CopyDbHelper
import com.example.doancoso3.model.Product
import com.example.doancoso3.viewmodel.CartViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doancoso3.viewmodel.FavoritesViewModel
import com.example.doancoso3.viewmodel.FavoritesViewModelFactory

class MainActivity : ComponentActivity() {
    private var db: CopyDbHelper? = null
    private val products = mutableListOf<Product>()
    private lateinit var cartViewModel: CartViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        // Khởi tạo CopyDbHelper
        val dbHelper = CopyDbHelper(this)
        dbHelper.openDatabase() // Đảm bảo cơ sở dữ liệu đã mở trước khi sử dụng

        // Khởi tạo ViewModelFactory và ViewModel
        val factory = FavoritesViewModelFactory(dbHelper)
        favoritesViewModel = ViewModelProvider(this, factory).get(FavoritesViewModel::class.java)
        setContent {
            Doancoso3Theme {
                AppContent()
            }
        }
        db = CopyDbHelper(this)
        db?.openDatabase()
    }

    @Composable
    fun AppContent() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home_screen") { HomeScreen(navController) }
            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                val product = findProductById(productId)

                if (product != null) {
                    ProductDetailScreen(product, cartViewModel, favoritesViewModel, navController)

                } else {
                    navController.navigate("home_screen") {
                        popUpTo("home_screen") { inclusive = true }
                    }
                }
            }
            composable("cartScreen") {
                CartScreen(cartViewModel, navController)
            }
            composable("favorite_screen") {
                FavoriteScreen(favoritesViewModel, cartViewModel, navController)
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