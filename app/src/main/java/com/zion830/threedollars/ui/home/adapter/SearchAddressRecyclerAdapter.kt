package com.zion830.threedollars.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.threedollar.network.data.kakao.Document
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemSearchByAddressBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import com.threedollar.common.listener.OnItemClickListener

class SearchAddressRecyclerAdapter(
    private val clickListener: OnItemClickListener<Document>
) : ListAdapter<Document, BaseViewHolder<ItemSearchByAddressBinding, Document>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemSearchByAddressBinding, Document> {
        return object : BaseViewHolder<ItemSearchByAddressBinding, Document>(R.layout.item_search_by_address, parent) {

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSearchByAddressBinding, Document>, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}