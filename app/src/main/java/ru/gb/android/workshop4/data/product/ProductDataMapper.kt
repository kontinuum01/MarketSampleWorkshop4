package ru.gb.android.workshop4.data.product

import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class ProductDataMapper @Inject constructor() {
    @OptIn(InternalSerializationApi::class)
    fun toEntity(productDto: ProductDto): ProductEntity {
        return ProductEntity(
            id = productDto.id,
            name = productDto.name,
            image = productDto.image,
            price = productDto.price,
            isFavorite = productDto.isFavorite
        )
    }
}
