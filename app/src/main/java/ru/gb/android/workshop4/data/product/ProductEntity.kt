package ru.gb.android.workshop4.data.product

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi @Serializable
class ProductEntity (
    val id: String,
    val name: String,
    val image: String,
    val price: Double,
    val isFavorite: Boolean
)

