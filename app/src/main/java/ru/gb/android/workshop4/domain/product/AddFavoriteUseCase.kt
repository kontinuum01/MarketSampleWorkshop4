package ru.gb.android.workshop4.domain.product

import kotlinx.serialization.InternalSerializationApi
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.data.favorites.FavoritesRepository
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
class AddFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(favorite: FavoriteEntity) {
        favoritesRepository.addToFavorites(favorite)
    }
}
