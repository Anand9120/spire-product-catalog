package com.spirelab.productcatalog.data.repository

import com.spirelab.productcatalog.data.local.dao.CartDao
import com.spirelab.productcatalog.data.mapper.toCartEntity
import com.spirelab.productcatalog.data.mapper.toDomain
import com.spirelab.productcatalog.data.mapper.toDomainList
import com.spirelab.productcatalog.domain.model.CartItem
import com.spirelab.productcatalog.domain.model.CartSummary
import com.spirelab.productcatalog.domain.model.Product
import com.spirelab.productcatalog.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
            .map { it.toDomainList() }
            .flowOn(dispatcher)
    }

    override fun getCartSummary(): Flow<CartSummary> {
        return cartDao.getAllCartItems()
            .map { entities ->
                val items = entities.toDomainList()
                val totalCount = items.sumOf { it.quantity }
                val totalPrice = items.sumOf { it.totalPrice }
                CartSummary(
                    totalItems = totalCount,
                    totalPrice = totalPrice,
                    items = items
                )
            }
            .flowOn(dispatcher)
    }

    override fun observeCartItem(productId: Int): Flow<CartItem?> {
        return cartDao.observeCartItem(productId)
            .map { it?.toDomain() }
            .flowOn(dispatcher)
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        withContext(dispatcher) {
            val existing = cartDao.getCartItemById(product.id)
            if (existing != null) {
                val newQty = (existing.quantity + quantity).coerceAtMost(
                    if (product.stock > 0) product.stock else 99
                )
                cartDao.update(existing.copy(quantity = newQty))
            } else {
                val initialQty = quantity.coerceAtLeast(1).coerceAtMost(
                    if (product.stock > 0) product.stock else 99
                )
                cartDao.insertOrUpdate(product.toCartEntity(initialQty))
            }
        }
    }

    override suspend fun increaseQuantity(productId: Int) {
        withContext(dispatcher) {
            val existing = cartDao.getCartItemById(productId) ?: return@withContext
            val maxStock = if (existing.stock > 0) existing.stock else 99
            if (existing.quantity < maxStock) {
                cartDao.update(existing.copy(quantity = existing.quantity + 1))
            }
        }
    }

    override suspend fun decreaseQuantity(productId: Int) {
        withContext(dispatcher) {
            val existing = cartDao.getCartItemById(productId) ?: return@withContext
            if (existing.quantity > 1) {
                cartDao.update(existing.copy(quantity = existing.quantity - 1))
            } else {
                cartDao.deleteById(productId)
            }
        }
    }

    override suspend fun updateQuantity(productId: Int, quantity: Int) {
        withContext(dispatcher) {
            if (quantity <= 0) {
                cartDao.deleteById(productId)
            } else {
                val existing = cartDao.getCartItemById(productId) ?: return@withContext
                val maxStock = if (existing.stock > 0) existing.stock else 99
                val validQty = quantity.coerceAtMost(maxStock)
                cartDao.update(existing.copy(quantity = validQty))
            }
        }
    }

    override suspend fun removeFromCart(productId: Int) {
        withContext(dispatcher) {
            cartDao.deleteById(productId)
        }
    }

    override suspend fun clearCart() {
        withContext(dispatcher) {
            cartDao.clearCart()
        }
    }

    override fun getTotalItemCount(): Flow<Int> {
        return cartDao.getTotalItemCount()
            .map { it ?: 0 }
            .flowOn(dispatcher)
    }
}
