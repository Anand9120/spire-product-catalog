package com.spirelab.productcatalog.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spirelab.productcatalog.di.AppContainer
import com.spirelab.productcatalog.ui.ViewModelFactory
import com.spirelab.productcatalog.ui.screens.cart.CartScreen
import com.spirelab.productcatalog.ui.screens.cart.CartViewModel
import com.spirelab.productcatalog.ui.screens.catalog.CatalogScreen
import com.spirelab.productcatalog.ui.screens.catalog.CatalogViewModel
import com.spirelab.productcatalog.ui.screens.details.ProductDetailsScreen
import com.spirelab.productcatalog.ui.screens.details.ProductDetailsViewModel

@Composable
fun AppNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Catalog.route,
        modifier = modifier
    ) {
        // Catalog Screen (Product Listing & Search)
        composable(
            route = Screen.Catalog.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            val catalogViewModel: CatalogViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            CatalogScreen(
                viewModel = catalogViewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        // Product Details Screen
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val detailsViewModel: ProductDetailsViewModel = viewModel(
                key = "details_$productId",
                factory = ViewModelFactory(appContainer, productId = productId)
            )
            ProductDetailsScreen(
                viewModel = detailsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        // Shopping Cart Screen (100% Offline Capable)
        composable(
            route = Screen.Cart.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            val cartViewModel: CartViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
