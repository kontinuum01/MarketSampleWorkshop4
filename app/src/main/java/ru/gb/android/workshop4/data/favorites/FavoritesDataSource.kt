package ru.gb.android.workshop4.data.favorites

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface FavoritesDataSource {
    fun consumeFavorites(): Flow<List<FavoriteEntity>>
    suspend fun saveFavorite(favoriteEntity: FavoriteEntity)
    suspend fun removeFavorite(favoriteEntity: FavoriteEntity)
}

class FavoritesDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : FavoritesDataSource {

    override fun consumeFavorites(): Flow<List<FavoriteEntity>> = dataStore.data
        .map(::mapFromPrefs)

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveFavorite(favoriteEntity: FavoriteEntity) {
        dataStore.edit { prefs ->
            val currentFavorites = mapFromPrefs(prefs).toMutableSet()
            currentFavorites.add(favoriteEntity)
            prefs[preferencesKey] = Json.encodeToString(currentFavorites.toList())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun removeFavorite(favoriteEntity: FavoriteEntity) {
        dataStore.edit { prefs ->
            val currentFavorites = mapFromPrefs(prefs).toMutableSet()
            currentFavorites.removeIf { it.id == favoriteEntity.id }
            prefs[preferencesKey] = Json.encodeToString(currentFavorites.toList())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun mapFromPrefs(prefs: Preferences): List<FavoriteEntity> =
        prefs[preferencesKey]
            ?.takeIf(String::isNotEmpty)
            ?.let { Json.decodeFromString(it) }
            ?: listOf()

    private val preferencesKey = stringPreferencesKey(KEY)

    private companion object {
        const val KEY = "favorite_key"
    }
}
