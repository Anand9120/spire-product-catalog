package com.spirelab.productcatalog.di

import android.content.Context
import com.spirelab.productcatalog.data.local.AppDatabase
import com.spirelab.productcatalog.data.remote.RetrofitClient
import com.spirelab.productcatalog.data.repository.CartRepositoryImpl
import com.spirelab.productcatalog.data.repository.ProductRepositoryImpl
import com.spirelab.productcatalog.domain.repository.CartRepository
import com.spirelab.productcatalog.domain.repository.ProductRepository

interface AppContainer {
    val productRepository: ProductRepository
    val cartRepository: CartRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            api = RetrofitClient.apiService
        )
    }

    override val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(
            cartDao = database.cartDao()
        )
    }
}
