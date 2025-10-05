package ru.gb.android.workshop4.domain.product

import kotlinx.serialization.InternalSerializationApi
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.data.product.ProductDto
import ru.gb.android.workshop4.data.product.ProductEntity
import javax.inject.Inject

class ProductDomainMapper @Inject constructor() {
    fun fromDto(productDto: ProductDto): Product {
        return Product(
            id = productDto.id,
            name = productDto.name,
            image = productDto.image,
            price = productDto.price,
            isFavorite = productDto.isFavorite

        )
    }

    @OptIn(InternalSerializationApi::class)
    fun fromEntity(productEntity: ProductEntity): Product {
        return Product(
            id = productEntity.id,
            name = productEntity.name,
            image = productEntity.image,
            price = productEntity.price,
            isFavorite = productEntity.isFavorite


        )
    }

    @OptIn(InternalSerializationApi::class)
    fun toEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            name = product.name,
            image = product.image,
            price = product.price,
            isFavorite = product.isFavorite

        )
    }

}
