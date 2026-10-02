package com.spirelab.productcatalog.ui.screens.cart

import com.spirelab.productcatalog.domain.model.CartItem
import java.util.Locale

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val formattedTotalPrice: String
        get() = String.format(Locale.US, "$%.2f", totalPrice)
}
