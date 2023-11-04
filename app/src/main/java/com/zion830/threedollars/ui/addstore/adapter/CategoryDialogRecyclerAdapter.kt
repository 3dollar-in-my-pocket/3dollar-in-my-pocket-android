package com.zion830.threedollars.ui.addstore.adapter

//class CategoryDialogRecyclerAdapter(
//    private val onClicked: (Int) -> Unit
//) : RecyclerView.Adapter<BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>>() {
//    val items: ArrayList<SelectedCategory> = arrayListOf()
//
//    fun getSelectedCount() = items.count { it.isSelected }
//
//    fun setItems(items: List<SelectedCategory>) {
//        this.items.clear()
//        this.items.addAll(items)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory> {
//        return object : BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>(R.layout.item_selected_category, parent) {
//            override fun bind(item: SelectedCategory, listener: OnItemClickListener<SelectedCategory>?) {
//                super.bind(item, listener)
//                binding.ibDeleteCategory.isVisible = false
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>, position: Int) {
//        return holder.bind(items[position], object : OnItemClickListener<SelectedCategory> {
//            override fun onClick(item: SelectedCategory) {
//                items[position] = SelectedCategory(!item.isSelected, item.menuType)
//                onClicked(getSelectedCount())
//                notifyItemChanged(position)
//            }
//        })
//    }
//}