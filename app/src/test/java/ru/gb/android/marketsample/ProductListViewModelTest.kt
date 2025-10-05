package ru.gb.android.marketsample

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
import ru.gb.android.workshop4.domain.product.AddFavoriteUseCase
import ru.gb.android.workshop4.domain.product.ConsumeFavoritesUseCase
import ru.gb.android.workshop4.domain.product.ConsumeProductsUseCase
import ru.gb.android.workshop4.domain.product.Product
import ru.gb.android.workshop4.domain.product.RemoveFavoriteUseCase
import ru.gb.android.workshop4.marketsample.R
import ru.gb.android.workshop4.presentation.product.ProductListViewModel
import ru.gb.android.workshop4.presentation.product.ProductState
import ru.gb.android.workshop4.presentation.product.ProductStateFactory

@RunWith(MockitoJUnitRunner::class)
class ProductListViewModelTest {

    private lateinit var sut: ProductListViewModel

    @Mock
    lateinit var consumeFavoritesUseCase: ConsumeFavoritesUseCase

    @Mock
    lateinit var productStateFactory: ProductStateFactory

    @Mock
    lateinit var removeFavoriteUseCase: RemoveFavoriteUseCase

    @Mock
    lateinit var addFavoriteUseCase: AddFavoriteUseCase

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        sut = ProductListViewModel(
            productStateFactory = productStateFactory,
            removeFavoriteUseCase = removeFavoriteUseCase,
            addFavoriteUseCase = addFavoriteUseCase,
            consumeFavoritesUseCase = consumeFavoritesUseCase
        )
    }

    @Test
    fun `requestProducts WHEN starting loading EXPECT isLoading flag in state `() {
        // arrange
        whenever(consumeFavoritesUseCase.invoke()).thenReturn(flowOf())

        // act
        sut.requestProducts()

        // assert
        Assert.assertTrue(sut.state.value.isLoading)
    }

    @Test
    fun `requestProducts WHEN product is loaded EXPECT reset isLoading flag and set product state`() {
        // arrange
        val expectedProducts = load2Products()

        // act
        sut.requestProducts()

        // assert
        Assert.assertFalse(sut.state.value.isLoading)
        assertEquals(expectedProducts, sut.state.value.productListState)
    }

    @Test
    fun `requestProducts WHEN product loading has error EXPECT state has en error`() {
        // arrange
        whenever(consumeFavoritesUseCase.invoke()).thenReturn(flow { throw IllegalStateException() })

        // act
        sut.requestProducts()

        // assert
        Assert.assertTrue(sut.state.value.hasError)
        assertEquals(R.string.error_wile_loading_data, sut.state.value.errorRes)
    }

    private fun load2Products(): List<ProductState> {
        whenever(consumeFavoritesUseCase.invoke())
            .thenReturn(
                flowOf(
                    listOf(
                        Product(id = "1"), Product(
                            id = "2",
                        )
                    )
                )
            )

        val state1 = ProductState(id = "1")
        val state2 = ProductState(id = "2")

        whenever(productStateFactory.create(argThat { id == "1" })).thenReturn(state1)
        whenever(productStateFactory.create(argThat { id == "2" })).thenReturn(state2)

        return listOf(state1, state2)
    }
}
