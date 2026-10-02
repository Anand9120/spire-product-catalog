package com.spirelab.productcatalog.domain.model

import java.util.Locale

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String? = null,
    val thumbnail: String,
    val images: List<String> = emptyList(),
    val availabilityStatus: String = "In Stock",
    val warrantyInformation: String = "",
    val shippingInformation: String = "",
    val returnPolicy: String = ""
) {
    val formattedPrice: String
        get() = String.format(Locale.US, "$%.2f", price)

    val discountedPrice: Double
        get() {
            if (discountPercentage <= 0.0) return price
            return price * (1.0 - discountPercentage / 100.0)
        }

    val formattedDiscountedPrice: String
        get() = String.format(Locale.US, "$%.2f", discountedPrice)

    val formattedRating: String
        get() = String.format(Locale.US, "%.1f", rating)

    val isInStock: Boolean
        get() = stock > 0
}
