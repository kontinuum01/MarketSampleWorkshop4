package ru.gb.android.workshop4.presentation.product.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.gb.android.workshop4.data.favorites.FavoriteEntity
import ru.gb.android.workshop4.marketsample.R
import ru.gb.android.workshop4.marketsample.databinding.ItemProductBinding
import ru.gb.android.workshop4.presentation.common.bump
import ru.gb.android.workshop4.presentation.product.ProductState

class ProductHolder(
    private val binding: ItemProductBinding,
    private val onAddToFavorites: (String) -> Unit,
    private val onRemoveFromFavorites: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productState: ProductState) {
        binding.image.load(productState.image)
        binding.name.text = productState.name
        binding.price.text =
            binding.root.resources.getString(R.string.price_with_arg, productState.price)

        bindFavorite(productState)
    }

    fun bindFavorite(productState: ProductState) {
        if (productState.isFavorite) {
            binding.favorite.setImageResource(R.drawable.ic_favorite_on)
            binding.favorite.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.reddish))
            binding.favorite.setOnClickListener {
                onRemoveFromFavorites(productState.id)
                binding.favorite.bump()
            }
        } else {
            binding.favorite.setImageResource(R.drawable.ic_favorite_off)
            binding.favorite.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.grey))
            binding.favorite.setOnClickListener {
                onAddToFavorites(productState.id)
                binding.favorite.bump()
            }
        }
    }
}
