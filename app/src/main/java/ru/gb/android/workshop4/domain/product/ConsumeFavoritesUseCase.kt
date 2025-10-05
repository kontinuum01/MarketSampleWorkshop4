package ru.gb.android.workshop4.domain.product


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.InternalSerializationApi
import ru.gb.android.workshop4.data.favorites.FavoritesRepository
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
class ConsumeFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val consumeProductsUseCase: ConsumeProductsUseCase,
    ) {
    operator fun invoke(): Flow<List<Product>> {
        return combine(
            consumeProductsUseCase(),
            favoritesRepository.consumeFavorites()
        ) { products, favoriteEntities ->
            val favoriteIds = favoriteEntities.map { it.id }.toSet()
            products.map { it.copy(isFavorite = it.id in favoriteIds) }
        }
    }
}












