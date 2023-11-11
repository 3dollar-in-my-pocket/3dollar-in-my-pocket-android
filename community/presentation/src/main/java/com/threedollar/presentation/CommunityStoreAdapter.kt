package com.threedollar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.data.PopularStore
import com.threedollar.presentation.databinding.ItemStoreBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

class CommunityStoreAdapter(private val clickStore: (PopularStore) -> Unit) :
    ListAdapter<PopularStore, CommunityStoreViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityStoreViewHolder {
        return CommunityStoreViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommunityStoreViewHolder, position: Int) {
        holder.onBind(getItem(position), clickStore)
    }
}

class CommunityStoreViewHolder(private val binding: ItemStoreBinding) : ViewHolder(binding.root) {
    fun onBind(popularStore: PopularStore, clickStore: (PopularStore) -> Unit) {
        binding.imgStoreIcon.loadUrlImg(popularStore.categories.first().imageUrl)
        binding.twStoreCategory.text = popularStore.categories.joinToString("") { "#${it.name}  " }
        binding.twStoreName.text = popularStore.storeName
        binding.root.onSingleClick { clickStore(popularStore) }
    }
}