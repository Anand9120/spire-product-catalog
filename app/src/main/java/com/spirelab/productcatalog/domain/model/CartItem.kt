package com.spirelab.productcatalog.domain.model

import java.util.Locale

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val stock: Int = 99,
    val brand: String? = null,
    val category: String? = null,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = price * quantity

    val formattedPrice: String
        get() = String.format(Locale.US, "$%.2f", price)

    val formattedTotalPrice: String
        get() = String.format(Locale.US, "$%.2f", totalPrice)
}

data class CartSummary(
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val items: List<CartItem> = emptyList()
) {
    val formattedTotalPrice: String
        get() = String.format(Locale.US, "$%.2f", totalPrice)

    val isEmpty: Boolean
        get() = items.isEmpty()
}
