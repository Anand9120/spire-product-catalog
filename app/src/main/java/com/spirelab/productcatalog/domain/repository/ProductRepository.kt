package com.spirelab.productcatalog.domain.repository

import com.spirelab.productcatalog.domain.model.Product
import com.spirelab.productcatalog.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<Resource<List<Product>>>
    fun searchProducts(query: String): Flow<Resource<List<Product>>>
    fun getProductById(id: Int): Flow<Resource<Product>>
}
