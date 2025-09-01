package ru.gb.android.workshop4.presentation.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.marketsample.databinding.ItemProductBinding
import ru.gb.android.workshop4.presentation.product.ProductState

class ProductsAdapter(
    private val onAddToFavorites: (String) -> Unit,
    private val onRemoveFromFavorites: (String) -> Unit,
) :
    ListAdapter<ProductState, ProductHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(
            binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onAddToFavorites = onAddToFavorites,
            onRemoveFromFavorites = onRemoveFromFavorites
        )
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val entity = getItem(position)
        entity?.let {
            holder.bind(entity)
        }
    }

    override fun onBindViewHolder(
        holder: ProductHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] == true) {
                holder.bindFavorite(getItem(position))
            }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<ProductState>() {

    override fun areItemsTheSame(oldItem: ProductState, newItem: ProductState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductState, newItem: ProductState): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ProductState, newItem: ProductState): Any? {
        if (oldItem.isFavorite != newItem.isFavorite) {
            return true
        }

        return null
    }
}
