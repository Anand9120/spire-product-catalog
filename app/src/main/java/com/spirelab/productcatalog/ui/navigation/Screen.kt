package com.spirelab.productcatalog.ui.navigation

sealed class Screen(val route: String) {
    object Catalog : Screen("catalog")
    object ProductDetails : Screen("details/{productId}") {
        fun createRoute(productId: Int): String = "details/$productId"
    }
    object Cart : Screen("cart")
}
