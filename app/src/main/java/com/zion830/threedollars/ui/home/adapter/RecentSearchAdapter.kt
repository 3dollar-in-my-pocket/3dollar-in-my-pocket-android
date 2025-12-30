package com.zion830.threedollars.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.place.PlaceModel
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.databinding.ItemRecentSearchBinding
import zion830.com.common.base.BaseDiffUtilCallback

class RecentSearchAdapter(
    private val searchClickListener: OnItemClickListener<PlaceModel>,
    private val deleteClickListener: OnItemClickListener<PlaceModel>,
) :
    PagingDataAdapter<PlaceModel, RecentSearchViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecentSearchViewHolder(ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(
            item = getItem(position) as PlaceModel,
            searchClickListener = searchClickListener,
            deleteClickListener = deleteClickListener
        )
    }
}

class RecentSearchViewHolder(private val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: PlaceModel,
        searchClickListener: OnItemClickListener<PlaceModel>,
        deleteClickListener: OnItemClickListener<PlaceModel>,
    ) {
        binding.tvAddress.text = item.placeName
        binding.tvAddressDetail.text = item.roadAddressName
        binding.recentSearchConstraintLayout.setOnClickListener {
            searchClickListener.onClick(item)
        }
        binding.deleteButton.setOnClickListener {
            deleteClickListener.onClick(item)
        }
    }
}