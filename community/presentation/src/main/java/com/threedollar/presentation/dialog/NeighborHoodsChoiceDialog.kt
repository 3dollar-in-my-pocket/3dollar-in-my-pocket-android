package com.threedollar.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.data.NeighborhoodModel
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.presentation.R
import com.threedollar.common.R as CommonR
import com.threedollar.presentation.databinding.DialogNeighborChoiceBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import javax.inject.Inject

@AndroidEntryPoint
class NeighborHoodsChoiceDialog : BottomSheetDialogFragment() {
    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    private lateinit var binding: DialogNeighborChoiceBinding

    private lateinit var selectNeighborhoodDistrict: String
    private var neighborhoodModels: List<NeighborhoodModel> = listOf()

    private var selectClick: (Neighborhoods.Neighborhood.District) -> Unit = {}
    private val descriptionChoiceAdapter by lazy {
        DescriptionChoiceAdapter {
            binding.districtBackImageView.isVisible = true
            binding.recyclerDescription.isVisible = false
            binding.recyclerDistrict.isVisible = true
            binding.titleTextView.text = it.description

            districtChoiceAdapter.submitList(it.districts)
            districtChoiceAdapter.setSelectDistrict(selectNeighborhoodDistrict)
        }
    }
    private val districtChoiceAdapter by lazy {
        DistrictChoiceAdapter {
            selectNeighborhoodDistrict = it.district
            sharedPrefUtils.saveSelectNeighborhoodDescription(it.description)
            sharedPrefUtils.saveSelectNeighborhoodDistrict(selectNeighborhoodDistrict)
            selectClick(it)
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogNeighborChoiceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectNeighborhoodDistrict = sharedPrefUtils.getSelectNeighborhoodDistrict()
        binding.recyclerDescription.adapter = descriptionChoiceAdapter
        binding.recyclerDistrict.adapter = districtChoiceAdapter

        descriptionChoiceAdapter.submitList(neighborhoodModels)
        descriptionChoiceAdapter.setSelectNeighborhood(selectNeighborhoodDistrict)
        binding.closeImageView.onSingleClick { dismiss() }
        binding.districtBackImageView.onSingleClick {
            binding.districtBackImageView.isVisible = false
            binding.recyclerDescription.isVisible = true
            binding.recyclerDistrict.isVisible = false
            binding.titleTextView.text = getString(CommonR.string.str_neighbor_title)
        }
    }

    fun setNeighborhoodModels(neighborhoodModels: List<NeighborhoodModel>): NeighborHoodsChoiceDialog {
        this.neighborhoodModels = neighborhoodModels
        return this
    }

    fun setItemClick(click: (Neighborhoods.Neighborhood.District) -> Unit): NeighborHoodsChoiceDialog {
        selectClick = click
        return this
    }
}