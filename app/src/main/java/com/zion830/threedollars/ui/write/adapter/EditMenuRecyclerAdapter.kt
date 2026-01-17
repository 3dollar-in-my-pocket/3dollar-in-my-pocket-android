package com.zion830.threedollars.ui.write.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import zion830.com.common.base.BaseDiffUtilCallback

class EditMenuRecyclerAdapter : ListAdapter<UserStoreMenuModel, MenuEditViewHolder>((BaseDiffUtilCallback())) {

    data class MutableMenuData(var name: String, var price: String)

    private val menuDataList = mutableListOf<MutableMenuData>()

    fun setMenuData(items: List<UserStoreMenuModel>) {
        menuDataList.clear()
        menuDataList.addAll(items.map {
            MutableMenuData(
                name = it.name ?: "",
                price = extractNumbers(it.price)
            )
        })
        submitList(items.toList())
    }

    private fun extractNumbers(input: String?): String {
        return input?.replace(Regex("[^0-9]"), "") ?: ""
    }

    fun addNewRow() {
        menuDataList.add(MutableMenuData("", ""))
        submitList(currentList + UserStoreMenuModel(name = "", price = ""))
    }

    fun getMenuData(): List<MutableMenuData> = menuDataList.toList()

    override fun onBindViewHolder(holder: MenuEditViewHolder, position: Int) {
        val menuData = menuDataList.getOrNull(position) ?: MutableMenuData("", "")
        holder.bind(menuData)
        holder.binding.etPrice.setOnFocusChangeListener { _, focused ->
            if (focused && position == currentList.size - 1) {
                addNewRow()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuEditViewHolder =
        MenuEditViewHolder(binding = ItemMenuEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}

class MenuEditViewHolder(val binding: ItemMenuEditBinding) : ViewHolder(binding.root) {
    private var nameWatcher: TextWatcher? = null
    private var priceWatcher: TextWatcher? = null

    fun bind(data: EditMenuRecyclerAdapter.MutableMenuData) {
        nameWatcher?.let { binding.etName.removeTextChangedListener(it) }
        priceWatcher?.let { binding.etPrice.removeTextChangedListener(it) }

        binding.etName.setText(data.name)
        binding.etPrice.setText(data.price)

        nameWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                data.name = s?.toString() ?: ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        priceWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                data.price = s?.toString() ?: ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etName.addTextChangedListener(nameWatcher)
        binding.etPrice.addTextChangedListener(priceWatcher)
    }
}
