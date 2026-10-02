package com.spirelab.productcatalog.data.mapper

import com.spirelab.productcatalog.data.model.ProductDto
import com.spirelab.productcatalog.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        category = category,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        thumbnail = thumbnail,
        images = images,
        availabilityStatus = availabilityStatus ?: if (stock > 0) "In Stock" else "Out of Stock",
        warrantyInformation = warrantyInformation ?: "",
        shippingInformation = shippingInformation ?: "",
        returnPolicy = returnPolicy ?: ""
    )
}

fun List<ProductDto>.toDomain(): List<Product> = map { it.toDomain() }
