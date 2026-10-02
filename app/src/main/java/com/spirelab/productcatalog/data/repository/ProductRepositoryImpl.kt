package com.spirelab.productcatalog.data.repository

import com.spirelab.productcatalog.data.mapper.toDomain
import com.spirelab.productcatalog.data.remote.DummyJsonApi
import com.spirelab.productcatalog.domain.model.Product
import com.spirelab.productcatalog.domain.repository.ProductRepository
import com.spirelab.productcatalog.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProductRepositoryImpl(
    private val api: DummyJsonApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    override fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getProducts(limit = 100)
            val products = response.products.toDomain()
            emit(Resource.Success(products))
        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection. Please check your network and retry.", e))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Connection timed out. Please try again.", e))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error (${e.code()}). Please try again later.", e))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unable to connect"}", e))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load products: ${e.localizedMessage ?: "Unknown error"}", e))
        }
    }.flowOn(dispatcher)

    override fun searchProducts(query: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            val trimmed = query.trim()
            val response = if (trimmed.isBlank()) {
                api.getProducts(limit = 100)
            } else {
                api.searchProducts(query = trimmed, limit = 100)
            }
            val products = response.products.toDomain()
            emit(Resource.Success(products))
        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection. Please check your network and retry.", e))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Connection timed out. Please try again.", e))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error (${e.code()}). Please try again later.", e))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unable to connect"}", e))
        } catch (e: Exception) {
            emit(Resource.Error("Search failed: ${e.localizedMessage ?: "Unknown error"}", e))
        }
    }.flowOn(dispatcher)

    override fun getProductById(id: Int): Flow<Resource<Product>> = flow {
        emit(Resource.Loading)
        try {
            val dto = api.getProductById(id)
            emit(Resource.Success(dto.toDomain()))
        } catch (e: UnknownHostException) {
            emit(Resource.Error("No internet connection. Please check your network and retry.", e))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Connection timed out. Please try again.", e))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error (${e.code()}). Please try again later.", e))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unable to connect"}", e))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load product details: ${e.localizedMessage ?: "Unknown error"}", e))
        }
    }.flowOn(dispatcher)
}
