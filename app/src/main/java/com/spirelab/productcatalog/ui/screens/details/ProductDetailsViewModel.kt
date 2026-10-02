package com.spirelab.productcatalog.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spirelab.productcatalog.domain.repository.CartRepository
import com.spirelab.productcatalog.domain.repository.ProductRepository
import com.spirelab.productcatalog.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productId: Int,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
        observeCartItem()
        observeTotalCartCount()
    }

    fun loadProduct() {
        viewModelScope.launch {
            productRepository.getProductById(productId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                product = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeCartItem() {
        viewModelScope.launch {
            cartRepository.observeCartItem(productId).collectLatest { item ->
                _uiState.update {
                    it.copy(quantityInCart = item?.quantity ?: 0)
                }
            }
        }
    }

    private fun observeTotalCartCount() {
        viewModelScope.launch {
            cartRepository.getTotalItemCount().collectLatest { count ->
                _uiState.update {
                    it.copy(totalCartCount = count)
                }
            }
        }
    }

    fun onAddToCart(quantity: Int = 1) {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            cartRepository.addToCart(product, quantity)
        }
    }

    fun onIncreaseQuantity() {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
        }
    }

    fun onDecreaseQuantity() {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(productId)
        }
    }

    fun onSelectImage(index: Int) {
        _uiState.update { it.copy(selectedImageIndex = index) }
    }

    fun onRetry() {
        loadProduct()
    }
}
