package com.spirelab.productcatalog.data.model

import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("products") val products: List<ProductDto> = emptyList(),
    @SerializedName("total") val total: Int = 0,
    @SerializedName("skip") val skip: Int = 0,
    @SerializedName("limit") val limit: Int = 0
)

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("discountPercentage") val discountPercentage: Double = 0.0,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("thumbnail") val thumbnail: String = "",
    @SerializedName("images") val images: List<String> = emptyList(),
    @SerializedName("availabilityStatus") val availabilityStatus: String? = null,
    @SerializedName("warrantyInformation") val warrantyInformation: String? = null,
    @SerializedName("shippingInformation") val shippingInformation: String? = null,
    @SerializedName("returnPolicy") val returnPolicy: String? = null
)
