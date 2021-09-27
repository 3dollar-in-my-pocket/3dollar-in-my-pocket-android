package com.zion830.threedollars.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemSearchByAddressBinding
import com.zion830.threedollars.repository.model.response.Addresse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchAddressRecyclerAdapter(
    private val clickListener: OnItemClickListener<Addresse>
) : ListAdapter<Addresse, BaseViewHolder<ItemSearchByAddressBinding, Addresse>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemSearchByAddressBinding, Addresse> {
        return object : BaseViewHolder<ItemSearchByAddressBinding, Addresse>(R.layout.item_search_by_address, parent) {

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSearchByAddressBinding, Addresse>, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}