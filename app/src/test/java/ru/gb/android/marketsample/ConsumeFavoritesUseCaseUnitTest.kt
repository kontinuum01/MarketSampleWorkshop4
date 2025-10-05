package ru.gb.android.marketsample

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.data.favorites.FavoritesRepository
import ru.gb.android.workshop4.domain.product.ConsumeFavoritesUseCase
import ru.gb.android.workshop4.domain.product.ConsumeProductsUseCase
import ru.gb.android.workshop4.domain.product.Product


@RunWith(MockitoJUnitRunner::class)
class ConsumeFavoritesUseCaseUnitTest {

    private lateinit var sut: ConsumeFavoritesUseCase

    @Mock
    private lateinit var favoritesRepository: FavoritesRepository

    @Mock
    private lateinit var consumeProductsUseCase: ConsumeProductsUseCase

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        sut = ConsumeFavoritesUseCase(
            favoritesRepository = favoritesRepository,
            consumeProductsUseCase = consumeProductsUseCase
        )
    }

    @OptIn(InternalSerializationApi::class)
    @Test
    fun `invoke sets isFavorite true WHEN product id in favorites`() = runTest {
        //arrange(подготовка данных)
        val products = listOf(
            Product(id = "1", name = "Prod1", isFavorite = false),
            Product(id = "2", name = "Prod2", isFavorite = false)
        )
        val favorites = listOf(
            FavoriteEntity(id = "1")
        )

        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(favorites))  //мокаем зависимости
        whenever(consumeProductsUseCase()).thenReturn(flowOf(products))  //мокаем зависимости

        //act (выполняем)
        val result = sut().first()

        //assert (проверяем)
        assertEquals(2, result.size)
        val p1 = result.first { it.id == "1" }
        val p2 = result.first { it.id == "2" }
        assertTrue(p1.isFavorite)   // id "1" в favorites
        assertFalse(p2.isFavorite)  // id "2" не в favorites
    }

    @OptIn(InternalSerializationApi::class)
    @Test
    fun `invoke sets all isFavorite false WHEN favorites empty`() = runTest {
        //arrange(подготовка данных)
        val products = listOf(
            Product(id = "1", name = "Prod1", isFavorite = false),
            Product(id = "2", name = "Prod2", isFavorite = false)
        )
        val favorites = emptyList<FavoriteEntity>()

        whenever(favoritesRepository.consumeFavorites()).thenReturn(flowOf(favorites))  //мокаем зависимости
        whenever(consumeProductsUseCase()).thenReturn(flowOf(products))  //мокаем зависимости

        //act (выполняем)
        val result = sut().first()

        //assert (проверяем)
        assertEquals(2, result.size)
        assertFalse(result[0].isFavorite)
        assertFalse(result[1].isFavorite)
    }
}