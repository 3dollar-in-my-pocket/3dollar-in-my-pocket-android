package com.zion830.threedollars.ui.home.adapter

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.zion830.threedollars.databinding.ItemHomeFilterChipBinding
import com.zion830.threedollars.ui.home.data.ChipAction
import com.zion830.threedollars.ui.home.data.HomeFilterCellType
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

class HomeFilterAdapter(
    private val onCategoryClick: () -> Unit,
    private val onRadioClick: (paramKey: String, optionIndex: Int) -> Unit,
    private val onActionClick: (link: SDLinkModel) -> Unit,
    private val onCloseSelectedCategoryClick: () -> Unit,
) : ListAdapter<HomeFilterCellType, HomeFilterAdapter.ChipViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemHomeFilterChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChipViewHolder(private val binding: ItemHomeFilterChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeFilterCellType) {
            when (item) {
                is HomeFilterCellType.Chip -> bindChip(item.chip, item.action)
                is HomeFilterCellType.SelectedCategoryChip -> bindSelectedCategoryChip(item.chip, item.current)
                is HomeFilterCellType.Button -> bindButton(item)
            }
        }

        private fun bindChip(chip: SDChipModel, action: ChipAction) {
            applyText(chip.text)
            applyImage(chip.image?.url)
            applyChipBackground(chip.style)
            binding.closeButton.isVisible = false
            binding.root.setOnClickListener {
                when (action) {
                    is ChipAction.OpenCategoryFilter -> onCategoryClick()
                    is ChipAction.SelectRadio -> onRadioClick(action.paramKey, action.optionIndex)
                    is ChipAction.DeepLink -> onActionClick(action.link)
                }
            }
        }

        private fun bindSelectedCategoryChip(chip: SDChipModel, current: HomeFilterCurrentCategory?) {
            applyText(chip.text)
            applyImage(chip.image?.url)
            applyChipBackground(current?.style ?: chip.style ?: DEFAULT_SELECTED_CATEGORY_STYLE)
            binding.closeButton.isVisible = true
            val tintColor = parseColor(chip.text.fontColor, default = Color.parseColor("#FF858F"))
            val closeDrawable = ContextCompat.getDrawable(binding.root.context, DesignSystemR.drawable.ic_close_chip)
                ?.mutate()
                ?.also { DrawableCompat.setTint(it, tintColor) }
            binding.closeButton.setImageDrawable(closeDrawable)
            binding.closeButton.setOnClickListener { onCloseSelectedCategoryClick() }
            binding.root.setOnClickListener(null)
        }

        private fun bindButton(item: HomeFilterCellType.Button) {
            val button = item.button
            applyText(button.text)
            applyImage(button.image?.url)
            applyChipBackground(button.style)
            binding.closeButton.isVisible = false
            binding.root.setOnClickListener {
                button.link?.let(onActionClick)
            }
        }

        private fun applyText(text: SDTextModel?) {
            val raw = text?.text.orEmpty()
            binding.chipText.text = if (text?.isHtml == true) {
                HtmlCompat.fromHtml(raw, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else {
                raw
            }
            binding.chipText.setTextColor(parseColor(text?.fontColor, default = Color.parseColor("#5A5A5A")))
        }

        private fun applyImage(url: String?) {
            if (url.isNullOrBlank()) {
                binding.chipImage.isVisible = false
                binding.chipImage.setImageDrawable(null)
            } else {
                binding.chipImage.isVisible = true
                Glide.with(binding.chipImage).load(url).into(binding.chipImage)
            }
        }

        private fun applyChipBackground(style: SDSurfaceStyleModel?) {
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(10f)
                setColor(parseColor(style?.backgroundColor, default = Color.WHITE))
                val border = style?.border
                val width = (border?.width ?: 1.0).toFloat()
                val borderColor = parseColor(border?.color, default = Color.parseColor("#E4E4E4"))
                if (width > 0f) {
                    setStroke(dp(width).toInt(), borderColor)
                }
            }
            binding.chipContainer.background = drawable
        }

        private fun dp(value: Float): Float =
            value * binding.root.resources.displayMetrics.density

        private fun parseColor(color: String?, default: Int): Int = runCatching {
            if (color.isNullOrBlank()) default else Color.parseColor(color)
        }.getOrDefault(default)
    }

    class SpacingDecoration(private val spacingDp: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return
            val spacingPx = (spacingDp * view.resources.displayMetrics.density).toInt()
            if (position > 0) {
                outRect.left = spacingPx
            }
        }
    }

    companion object {
        private val DEFAULT_SELECTED_CATEGORY_STYLE = SDSurfaceStyleModel(
            backgroundColor = "#FFF3F4",
            border = SDBorderModel(color = "#FF858F", width = 1.0),
        )

        private val DIFF = object : DiffUtil.ItemCallback<HomeFilterCellType>() {
            override fun areItemsTheSame(oldItem: HomeFilterCellType, newItem: HomeFilterCellType): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: HomeFilterCellType, newItem: HomeFilterCellType): Boolean = oldItem == newItem
        }
    }
}
