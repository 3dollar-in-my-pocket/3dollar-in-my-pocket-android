package com.threedollar.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.presentation.databinding.DialogNeighborChoiceBinding
import zion830.com.common.base.onSingleClick

class NeighborHoodsChoiceDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogNeighborChoiceBinding
    private var choiceNeighborhood: Neighborhoods.Neighborhood? = null
    private var choiceClick: (Neighborhoods.Neighborhood) -> Unit = {}
    private val adapter by lazy {
        NeighborHoodsChoiceAdapter {
            choiceClick(it)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (choiceNeighborhood == null) {
            Toast.makeText(parentFragment?.requireContext(), "지역 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNeighborChoiceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgClose.onSingleClick { dismiss() }
        choiceNeighborhood?.let {
            adapter.setChoiceNeighborhood(it)
            binding.recyclerNeighbor.adapter = adapter
        } ?: run { dismiss() }
    }

    fun setChoiceNeighborhood(neighborhood: Neighborhoods.Neighborhood): NeighborHoodsChoiceDialog {
        choiceNeighborhood = neighborhood
        return this
    }

    fun setItemClick(click: (Neighborhoods.Neighborhood) -> Unit): NeighborHoodsChoiceDialog {
        choiceClick = click
        return this
    }

}