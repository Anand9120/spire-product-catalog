package com.spirelab.productcatalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spirelab.productcatalog.di.AppContainer
import com.spirelab.productcatalog.ui.screens.cart.CartViewModel
import com.spirelab.productcatalog.ui.screens.catalog.CatalogViewModel
import com.spirelab.productcatalog.ui.screens.details.ProductDetailsViewModel

class ViewModelFactory(
    private val appContainer: AppContainer,
    private val productId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CatalogViewModel::class.java) -> {
                CatalogViewModel(
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProductDetailsViewModel::class.java) -> {
                requireNotNull(productId) { "productId must be provided for ProductDetailsViewModel" }
                ProductDetailsViewModel(
                    productId = productId,
                    productRepository = appContainer.productRepository,
                    cartRepository = appContainer.cartRepository
                ) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(
                    cartRepository = appContainer.cartRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
