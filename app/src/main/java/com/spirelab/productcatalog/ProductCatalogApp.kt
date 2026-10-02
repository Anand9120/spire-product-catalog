package com.spirelab.productcatalog

import android.app.Application
import com.spirelab.productcatalog.di.AppContainer
import com.spirelab.productcatalog.di.DefaultAppContainer

class ProductCatalogApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
