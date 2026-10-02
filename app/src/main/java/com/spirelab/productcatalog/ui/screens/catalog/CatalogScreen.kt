package com.spirelab.productcatalog.ui.screens.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spirelab.productcatalog.domain.model.Product
import com.spirelab.productcatalog.ui.components.EmptyStateView
import com.spirelab.productcatalog.ui.components.ErrorStateView
import com.spirelab.productcatalog.ui.components.LoadingView
import com.spirelab.productcatalog.ui.components.ProductCard
import com.spirelab.productcatalog.ui.components.SearchBarComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onProductClick: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text(
                            text = "Product Catalog",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "SPIRE Lab Assessment",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (uiState.cartItemCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = "${uiState.cartItemCount}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            SearchBarComponent(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = viewModel::clearSearch
            )

            // Category Filter Chips
            if (uiState.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.categories) { category ->
                        val isSelected = (category == "All" && uiState.selectedCategory == null) ||
                                uiState.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onCategorySelect(if (category == "All") "" else category)
                            },
                            label = {
                                Text(
                                    text = category.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Body Content based on State
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.products.isEmpty() -> {
                        LoadingView(message = "Fetching catalog from DummyJSON...")
                    }

                    uiState.errorMessage != null && uiState.products.isEmpty() -> {
                        ErrorStateView(
                            message = uiState.errorMessage ?: "Failed to connect to the server.",
                            onRetry = viewModel::onRetry,
                            secondaryButtonText = if (uiState.cartItemCount > 0) "Open Offline Cart (${uiState.cartItemCount} items)" else null,
                            onSecondaryAction = if (uiState.cartItemCount > 0) onNavigateToCart else null
                        )
                    }

                    uiState.isEmptyResults -> {
                        EmptyStateView(
                            title = "No products found",
                            subtitle = if (uiState.searchQuery.isNotEmpty()) {
                                "No matches found for \"${uiState.searchQuery}\". Try another keyword."
                            } else {
                                "The product catalog is currently empty."
                            },
                            actionButtonText = if (uiState.searchQuery.isNotEmpty()) "Clear Search" else "Refresh",
                            onActionClick = if (uiState.searchQuery.isNotEmpty()) viewModel::clearSearch else viewModel::onRetry
                        )
                    }

                    else -> {
                        val displayProducts = uiState.filteredProducts

                        if (displayProducts.isEmpty() && uiState.selectedCategory != null) {
                            EmptyStateView(
                                title = "No items in category",
                                subtitle = "No products found under category \"${uiState.selectedCategory}\".",
                                actionButtonText = "Show All Products",
                                onActionClick = { viewModel.onCategorySelect("") }
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = displayProducts,
                                    key = { it.id }
                                ) { product ->
                                    val inCartQty = uiState.cartItemsMap[product.id] ?: 0
                                    ProductCard(
                                        product = product,
                                        onClick = { onProductClick(product.id) },
                                        onAddToCart = viewModel::onAddToCart,
                                        isInCart = inCartQty > 0,
                                        cartQuantity = inCartQty
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
