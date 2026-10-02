package com.spirelab.productcatalog.ui.screens.details

import com.spirelab.productcatalog.domain.model.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val errorMessage: String? = null,
    val quantityInCart: Int = 0,
    val totalCartCount: Int = 0,
    val selectedImageIndex: Int = 0
)
