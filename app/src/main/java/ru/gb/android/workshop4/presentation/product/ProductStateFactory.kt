package ru.gb.android.workshop4.presentation.product

import dagger.hilt.android.scopes.ViewModelScoped
import ru.gb.android.workshop4.domain.product.Product
import ru.gb.android.workshop4.presentation.common.PriceFormatter
import javax.inject.Inject

@ViewModelScoped
class ProductStateFactory @Inject constructor(
    private val priceFormatter: PriceFormatter,
) {
    fun create(product: Product): ProductState {
        return ProductState(
            id = product.id,
            name = product.name,
            image = product.image,
            price = product.price.let(priceFormatter::format),
            isFavorite = product.isFavorite
        )
    }
}