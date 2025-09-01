package ru.gb.android.workshop4.data.product

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("price") val price: Double,
//    @SerializedName("isFavorite") val isFavorite: Boolean
)