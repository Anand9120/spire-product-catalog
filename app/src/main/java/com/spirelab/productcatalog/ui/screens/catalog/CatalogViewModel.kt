package com.spirelab.productcatalog.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spirelab.productcatalog.domain.model.Product
import com.spirelab.productcatalog.domain.repository.CartRepository
import com.spirelab.productcatalog.domain.repository.ProductRepository
import com.spirelab.productcatalog.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        loadProducts()
        observeCart()
        observeSearch()
    }

    fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val allCategories = listOf("All") + result.data.map { it.category }
                            .distinct()
                            .sorted()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                products = result.data,
                                errorMessage = null,
                                categories = allCategories
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

    private fun observeSearch() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(350)
                .distinctUntilChanged()
                .collectLatest { query ->
                    executeSearch(query)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            val flow = if (query.isBlank()) {
                productRepository.getProducts()
            } else {
                productRepository.searchProducts(query)
            }
            flow.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                products = result.data,
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

    fun onCategorySelect(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = if (it.selectedCategory == category) null else category
            )
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collectLatest { cartItems ->
                val quantityMap = cartItems.associate { it.productId to it.quantity }
                val totalCount = cartItems.sumOf { it.quantity }
                _uiState.update {
                    it.copy(
                        cartItemCount = totalCount,
                        cartItemsMap = quantityMap
                    )
                }
            }
        }
    }

    fun onAddToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product, quantity = 1)
        }
    }

    fun onRetry() {
        if (_uiState.value.searchQuery.isNotBlank()) {
            executeSearch(_uiState.value.searchQuery)
        } else {
            loadProducts()
        }
    }
}
