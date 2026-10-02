package com.spirelab.productcatalog.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spirelab.productcatalog.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCartSummary().collectLatest { summary ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = summary.items,
                        totalItems = summary.totalItems,
                        totalPrice = summary.totalPrice
                    )
                }
            }
        }
    }

    fun onIncreaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
        }
    }

    fun onDecreaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(productId)
        }
    }

    fun onRemoveItem(productId: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }

    fun onClearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
