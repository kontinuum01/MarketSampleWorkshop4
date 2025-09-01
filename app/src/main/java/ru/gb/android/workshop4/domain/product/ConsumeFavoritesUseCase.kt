package ru.gb.android.workshop4.domain.product

import kotlinx.coroutines.flow.combine
import ru.gb.android.workshop4.data.favorites.FavoritesRepository
import javax.inject.Inject

class ConsumeFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val consumeProductsUseCase: ConsumeProductsUseCase,
    ) {
    operator fun invoke() {
        combine(
            consumeProductsUseCase(),
            favoritesRepository.consumeFavorites()
        ) { products, favoriteEntities ->
            val favoriteIds = favoriteEntities.map { it.id }.toSet()
            products.map { it.copy(isFavorite = it.id in favoriteIds) }
        }
    }
}

