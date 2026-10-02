package com.spirelab.productcatalog.domain.repository

import com.spirelab.productcatalog.domain.model.CartItem
import com.spirelab.productcatalog.domain.model.CartSummary
import com.spirelab.productcatalog.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    fun getCartSummary(): Flow<CartSummary>
    fun observeCartItem(productId: Int): Flow<CartItem?>
    suspend fun addToCart(product: Product, quantity: Int = 1)
    suspend fun increaseQuantity(productId: Int)
    suspend fun decreaseQuantity(productId: Int)
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun removeFromCart(productId: Int)
    suspend fun clearCart()
    fun getTotalItemCount(): Flow<Int>
}
