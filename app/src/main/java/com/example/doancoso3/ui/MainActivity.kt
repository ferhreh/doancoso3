package com.example.doancoso3.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doancoso3.data.ProductFirestoreRepository
import com.example.doancoso3.data.UserAddressFirestoreRepository
import com.example.doancoso3.data.UserFirestoreRepository
import com.example.doancoso3.model.Product
import com.example.doancoso3.model.User
import com.example.doancoso3.ui.theme.Doancoso3Theme
import com.example.doancoso3.viewmodel.*

class MainActivity : ComponentActivity() {

    private var userId: String = ""
    val languageViewModel: LanguageViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels { CartViewModelFactory() }
    private val addressViewModel: AddressViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels { OrderViewModelFactory(this) }

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent?.getStringExtra("USER_ID") ?: ""
        enableEdgeToEdge()

        favoritesViewModel = ViewModelProvider(
            this,
            FavoritesViewModelFactory(userId)
        )[FavoritesViewModel::class.java]

        setContent {
            Doancoso3Theme {
                AppContent()
            }
        }
    }

    @Composable
    fun AppContent() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }

            composable("home_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                HomeScreen(navController, userId, languageViewModel)
            }

            composable("productDetail/{productId}/{userId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                if (productId != null) {
                    ProductDetailScreen(userId, productId, cartViewModel, favoritesViewModel, navController,languageViewModel)
                }
            }

            composable("cartScreen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                CartScreen(cartViewModel, navController, userId,languageViewModel)
            }

            composable("favorite_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(userId)
                )

                FavoriteScreen(userId, favoritesViewModel, cartViewModel, navController,languageViewModel)
            }

            composable("checkoutScreen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                CheckoutScreen(navController, cartViewModel, userId, addressViewModel, orderViewModel,languageViewModel)
            }

            composable("saved_addresses/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                SavedAddressesScreen(navController, userId, addressViewModel)
            }

            composable("editAddressScreen/{addressId}/{userId}") { backStackEntry ->
                val addressId = backStackEntry.arguments?.getString("addressId") ?: ""
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                EditAddressScreen(navController, userId, addressId, addressViewModel,languageViewModel)
            }
            composable("add_address/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val addressRepo = UserAddressFirestoreRepository()

                AddAddressScreen(navController, userId, addressRepo,languageViewModel)
            }

            composable("order_success/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                OrderSuccessScreen(navController, userId,languageViewModel)
            }

            composable("profile_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val userRepo = UserFirestoreRepository()

                ProfileScreen(navController, userId, userRepo,languageViewModel)
            }
            composable("orders/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                OrderScreen(navController, userId, orderViewModel,languageViewModel)
            }

            composable("searchScreen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                val context = LocalContext.current
                val viewModelStoreOwner = remember { context as ViewModelStoreOwner }

                val searchViewModel: SearchViewModel = viewModel(
                    viewModelStoreOwner = viewModelStoreOwner,
                    factory = SearchViewModel.SearchViewModelFactory(userId)
                )

                SearchScreen(
                    navController = navController,
                    userId = userId,
                    searchViewModel = searchViewModel,
                    languageViewModel
                )
            }
            composable(
                "feedbackScreen/{userId}/{productId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("productId") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val productId = backStackEntry.arguments?.getString("productId") ?: ""

                val productRepository = remember { ProductFirestoreRepository() }
                val userRepository = remember { UserFirestoreRepository() }

                val productState = produceState<Product?>(initialValue = null) {
                    value = productRepository.getProductById(productId)
                }

                val userState = produceState<User?>(initialValue = null) {
                    value = userRepository.getUserById(userId)
                }

                if (productState.value == null || userState.value == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    FeedbackScreen(
                        navController = navController,
                        product = productState.value!!,
                        userId = userId,
                        userName = userState.value?.name ?: "",
                        languageViewModel
                    )
                }
            }
            composable("settings_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                SettingsScreen(navController = navController, userId = userId, languageViewModel = languageViewModel)
            }
        }
    }
}

