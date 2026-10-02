package com.spirelab.productcatalog.data.mapper

import com.spirelab.productcatalog.data.local.entity.CartItemEntity
import com.spirelab.productcatalog.domain.model.CartItem
import com.spirelab.productcatalog.domain.model.Product

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        stock = stock,
        brand = brand,
        category = category,
        addedAt = addedAt
    )
}

fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        stock = stock,
        brand = brand,
        category = category,
        addedAt = addedAt
    )
}

fun Product.toCartEntity(quantity: Int = 1): CartItemEntity {
    return CartItemEntity(
        productId = id,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        stock = stock,
        brand = brand,
        category = category,
        addedAt = System.currentTimeMillis()
    )
}

fun List<CartItemEntity>.toDomainList(): List<CartItem> = map { it.toDomain() }
