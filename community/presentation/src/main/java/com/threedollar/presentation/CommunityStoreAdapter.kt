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

class CommunityStoreAdapter(private val clickStore: () -> Unit) : ListAdapter<PopularStore, CommunityStoreViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityStoreViewHolder {
        return CommunityStoreViewHolder(ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommunityStoreViewHolder, position: Int) {
        holder.onBind(getItem(position), clickStore)
    }
}

class CommunityStoreViewHolder(private val binding: ItemStoreBinding) : ViewHolder(binding.root) {
    fun onBind(popularStore: PopularStore, clickStore: () -> Unit) {
        binding.imgStoreIcon.loadUrlImg(popularStore.storeType)
        binding.twStoreCategory.text = popularStore.categories.joinToString("#")
        binding.twStoreName.text = popularStore.storeName
        binding.root.onSingleClick { clickStore() }
    }
}