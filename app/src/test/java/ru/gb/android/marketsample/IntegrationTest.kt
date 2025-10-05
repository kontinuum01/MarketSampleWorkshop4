package ru.gb.android.marketsample


import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.InternalSerializationApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import ru.gb.android.workshop4.data.product.ProductDto
import ru.gb.android.workshop4.data.product.ProductRemoteDataSource
import ru.gb.android.workshop4.domain.product.AddFavoriteUseCase
import ru.gb.android.workshop4.domain.product.ConsumeFavoritesUseCase
import ru.gb.android.workshop4.domain.product.RemoveFavoriteUseCase
import ru.gb.android.workshop4.presentation.product.ProductListViewModel
import ru.gb.android.workshop4.presentation.product.ProductState
import ru.gb.android.workshop4.presentation.product.ProductStateFactory
import ru.gb.android.workshop4.presentation.product.ProductsScreenState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import org.junit.After
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import kotlin.test.DefaultAsserter.assertTrue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.dagger.hilt.android.testing.HiltAndroidRule
import com.google.dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.InjectMockKs


@OptIn(InternalSerializationApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class IntegrationTest {

    @InjectMockKs
    private lateinit var sut: ProductListViewModel

    private val testFavoriteId = "fav456"

    @Mock
    lateinit var mockAddFavoriteUseCase: AddFavoriteUseCase

    @Mock
    lateinit var mockRemoveFavoriteUseCase: RemoveFavoriteUseCase

    @Mock
    lateinit var mockConsumeFavoritesUseCase: ConsumeFavoritesUseCase

    @Mock
    lateinit var mockProductStateFactory: ProductStateFactory

    @Mock
    lateinit var productRemoteDataSource: ProductRemoteDataSource



    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAnddroidRule(this)

    private lateinit var ioDispatcher: TestDispatcher

    @Before
    fun setup() {
        hiltRule.inject()
        ioDispatcher = mainDispatcherRule.testDispatcher

        coEvery { mockProductStateFactory.create(any()) } returns mockk()

        coEvery { mockConsumeFavoritesUseCase() } returns flowOf(
            listOf(mockk(), mockk())
        )

        coEvery { mockAddFavoriteUseCase(any()) } returns Unit
        coEvery { mockRemoveFavoriteUseCase(any()) } returns Unit

        sut = ProductListViewModel(
            addFavoriteUseCase = mockAddFavoriteUseCase,
            removeFavoriteUseCase = mockRemoveFavoriteUseCase,
            consumeFavoritesUseCase = mockConsumeFavoritesUseCase,
            productStateFactory = mockProductStateFactory,
        )
    }

    @Test
    fun `requestProducts EXPECT show all three states`() = runTest {
        // arrange
        // настройка моков DataSource для ProductRemoteDataSource
        coEvery { productRemoteDataSource.getProducts() } returns listOf(
            makeProductDto(id = "1", price = 100.0),
            makeProductDto(id = "2", price = 200.0)
        )
        //настройка мока для ProductStateFactory
        val state1 = ProductState(id = "1", price = "100.00", isFavorite = false)
        val state2 = ProductState(id = "2", price = "200.00", isFavorite = false)
        coEvery { mockProductStateFactory.create(any()) } returns state1

        val expectedInitialState = ProductsScreenState()
        val expectedLoadingState = ProductsScreenState(isLoading = true)
        val expectedDataState = ProductsScreenState(
            isLoading = false,
            productListState = listOf(state1, state2)
        )

        // act
        sut.requestProducts()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // assert
        // проверяем начальное состояние
        assertEquals(expectedInitialState, sut.state.value)

        advanceUntilIdle()

        //проверяем состояние загрузки
        assertEquals(expectedLoadingState, sut.state.value)

        advanceUntilIdle()

        //проверяем конечное состояние данных
        assertEquals(expectedDataState, sut.state.value)

    }

    private fun makeProductDto(
        id: String = "",
        name: String = "",
        image: String = "",
        price: Double = 0.0,
    ): ProductDto {
        return ProductDto(
            id = id,
            name = name,
            image = image,
            price = price,
            isFavorite = false
        )
    }

    @Test
    fun `when requestProducts is called, state updates with product list`() = runTest {
        // act
        sut.requestProducts()

        advanceUntilIdle()

        //assert
        //проверка состояния
        val state = sut.state.value

        assertTrue("Loading should be false", !state.isLoading)
        assertTrue("Product list should not be empty", state.productListState.isNotEmpty())

        //проверка, что UseCase был вызван
        coVerify { mockConsumeFavoritesUseCase() }
    }

    @Test
    fun `addToFavorites ADDS product to favorites repository`() = runTest {
        // act
        sut.requestProducts()
        advanceUntilIdle()

        sut.addToFavorites(testFavoriteId)

        advanceUntilIdle()

        //assert
        //проверка, что UseCase был вызван
        coVerify(exactly = 1) {
            mockAddFavoriteUseCase(FavoriteEntity(testFavoriteId))
        }

        //проверка, что ошибки нет
        val state = sut.state.value
        assertTrue("Has error should be false", !state.hasError)
    }

    @Test
    fun `removeFromFavorites REMOVES product from favorites repository`() = runTest {
        // act
        sut.requestProducts()
        advanceUntilIdle()

        //вызов функции удаления из избранного
        sut.removeFromFavorites(testFavoriteId)

        advanceUntilIdle()

        //assert
        coVerify(exactly = 1) {
            mockRemoveFavoriteUseCase(FavoriteEntity(testFavoriteId))
        }

        //проверка, что ошибки нет
        val state = sut.state.value
        assertTrue("Has error should be false", !state.hasError)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}




















