package ru.gb.android.workshop4.presentation.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.domain.product.AddFavoriteUseCase
import ru.gb.android.workshop4.domain.product.ConsumeFavoritesUseCase
import ru.gb.android.workshop4.domain.product.ConsumeProductsUseCase
import ru.gb.android.workshop4.domain.product.RemoveFavoriteUseCase
import ru.gb.android.workshop4.marketsample.R
import javax.inject.Inject


@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val consumeProductsUseCase: ConsumeProductsUseCase,
    private val productStateFactory: ProductStateFactory,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    ) : ViewModel() {

    private val _state = MutableStateFlow(ProductsScreenState())
    val state: StateFlow<ProductsScreenState> = _state.asStateFlow()

    fun requestProducts() {
        consumeProductsUseCase()
            .map { products ->
                products.map { product -> productStateFactory.create(product) }
            }
            .onStart {
                _state.update { screenState -> screenState.copy(isLoading = true) }
            }
            .onEach { productListState ->
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        productListState = productListState,
                    )
                }
            }
            .catch {
                _state.update { screenState ->
                    screenState.copy(
                        hasError = true,
                        errorRes = R.string.error_wile_loading_data,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        requestProducts()
    }

    fun errorHasShown() {
        _state.update { screenState -> screenState.copy(hasError = false) }
    }

    fun addToFavorites(favoriteId: String) {
        viewModelScope.launch {
            try {
                addFavoriteUseCase(FavoriteEntity(favoriteId))
                Log.i("TAG","remove to favorite")
            } catch (e: Exception) {
                _state.update {
                    it.copy(hasError = true, errorRes = R.string.error_favorites_operation)
                }
            }
        }
    }

    fun removeFromFavorites(favoriteId: String) {
        viewModelScope.launch {
            try {
                removeFavoriteUseCase(FavoriteEntity(favoriteId))
                Log.i("TAG","remove to favorite")
            } catch (e: Exception) {
                _state.update {
                    it.copy(hasError = true, errorRes = R.string.error_favorites_operation)
                }
            }
        }
    }
}


