package ru.gb.android.marketsample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import ru.gb.android.workshop4.data.favorites.FavoritesDataSource
import ru.gb.android.workshop4.data.favorites.FavoritesRepository
import ru.gb.android.workshop4.data.product.ProductDataMapper
import ru.gb.android.workshop4.data.product.ProductDto
import ru.gb.android.workshop4.data.product.ProductEntity
import ru.gb.android.workshop4.data.product.ProductLocalDataSource
import ru.gb.android.workshop4.data.product.ProductRemoteDataSource
import ru.gb.android.workshop4.data.product.ProductRepository
import ru.gb.android.workshop4.domain.product.AddFavoriteUseCase
import ru.gb.android.workshop4.domain.product.ConsumeProductsUseCase
import ru.gb.android.workshop4.domain.product.ProductDomainMapper
import ru.gb.android.workshop4.domain.product.RemoveFavoriteUseCase
import ru.gb.android.workshop4.presentation.common.PriceFormatterImpl
import ru.gb.android.workshop4.presentation.product.ProductListViewModel
import ru.gb.android.workshop4.presentation.product.ProductState
import ru.gb.android.workshop4.presentation.product.ProductStateFactory
import ru.gb.android.workshop4.presentation.product.ProductsScreenState

class TestProductLocalDataSource : ProductLocalDataSource {
    private val state = MutableStateFlow<List<ProductEntity>>(listOf())
    override fun consumeProducts(): Flow<List<ProductEntity>> = state.asStateFlow()
    override suspend fun saveProducts(products: List<ProductEntity>) {
        state.value = products
    }
}

@RunWith(MockitoJUnitRunner::class)
class IntegrationTest {

    private lateinit var sut: ProductListViewModel

    @Mock
    lateinit var productRemoteDataSource: ProductRemoteDataSource

    @Mock
    lateinit var favoritesDataSource: FavoritesDataSource

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val ioDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        val productRepository = ProductRepository(
            productLocalDataSource = TestProductLocalDataSource(),
            productRemoteDataSource = productRemoteDataSource,
            productDataMapper = ProductDataMapper(),
            dispatcher = ioDispatcher,
        )
        val consumeProductsUseCase = ConsumeProductsUseCase(
            productRepository = productRepository,
            productDomainMapper = ProductDomainMapper(),
        )
        val favoritesRepository = FavoritesRepository(
            favoritesDataSource = favoritesDataSource,
            dispatcher = ioDispatcher
        )

        val addFavoriteUseCase = AddFavoriteUseCase(
            favoritesRepository = favoritesRepository
        )
        val removeFavoriteUseCase = RemoveFavoriteUseCase(
            favoritesRepository = favoritesRepository
        )

        sut = ProductListViewModel(
            addFavoriteUseCase = addFavoriteUseCase,
            removeFavoriteUseCase = removeFavoriteUseCase,
            consumeProductsUseCase = consumeProductsUseCase,
            productStateFactory = ProductStateFactory(priceFormatter = PriceFormatterImpl()),
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `requestProducts EXPECT show all three states`() = runTest(UnconfinedTestDispatcher()) {
        // arrange
        productsFromServer(create(id = "1", price = 100.0), create(id = "2", price = 200.0))
        val expectedInitialState = ProductsScreenState()
        val expectedLoadingState = ProductsScreenState(isLoading = true)
        val expectedDataState = ProductsScreenState(
            isLoading = false,
            productListState = listOf(
                ProductState(id = "1", price = "100.00"),
                ProductState(id = "2", price = "200.00"),
            )
        )
        val (job, results) = collectResults()

        // act
        sut.requestProducts()
        ioDispatcher.scheduler.runCurrent()
        mainDispatcherRule.testDispatcher.scheduler.runCurrent()

        // assert
        assertEquals(3, results.size)
        assertEquals(expectedInitialState, results[0])
        assertEquals(expectedLoadingState, results[1])
        assertEquals(expectedDataState, results[2])
        job.cancel()
    }

    private suspend fun productsFromServer(vararg products: ProductDto) {
        whenever(productRemoteDataSource.getProducts()).thenReturn(products.toList())
    }

    private fun create(
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
            )
    }

    private fun CoroutineScope.collectResults(): Pair<Job, List<ProductsScreenState>> {
        val results = mutableListOf<ProductsScreenState>()
        val job = sut.state
            .onEach(results::add)
            .launchIn(this)

        return (job to results)
    }
}
