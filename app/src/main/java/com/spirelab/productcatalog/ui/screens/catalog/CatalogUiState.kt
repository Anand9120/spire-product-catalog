package com.spirelab.productcatalog.ui.screens.catalog

import com.spirelab.productcatalog.domain.model.Product

data class CatalogUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val cartItemCount: Int = 0,
    val cartItemsMap: Map<Int, Int> = emptyMap()
) {
    val isEmptyResults: Boolean
        get() = !isLoading && errorMessage == null && products.isEmpty()

    val filteredProducts: List<Product>
        get() {
            if (selectedCategory == null || selectedCategory == "All") {
                return products
            }
            return products.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
}
