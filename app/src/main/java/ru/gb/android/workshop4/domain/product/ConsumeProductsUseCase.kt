package ru.gb.android.workshop4.domain.product

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import ru.gb.android.workshop4.data.product.ProductRepository
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
class ConsumeProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val productDomainMapper: ProductDomainMapper,
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.consumeProducts()
            .map { products -> products.map(productDomainMapper::fromEntity) }
    }
}