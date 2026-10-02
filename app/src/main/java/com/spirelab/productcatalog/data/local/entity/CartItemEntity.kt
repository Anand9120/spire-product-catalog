package com.spirelab.productcatalog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val stock: Int = 99,
    val brand: String? = null,
    val category: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)
