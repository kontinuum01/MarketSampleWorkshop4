package ru.gb.android.workshop4.domain.product

data class Product (
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val price: Double = 0.0,
    val isFavorite: Boolean = false
)
