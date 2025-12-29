package com.zion830.threedollars.ui.mypage.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.showSnack
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.databinding.FragmentMyMedalBinding
import com.zion830.threedollars.ui.dialog.MedalInfoDialog
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.zion830.threedollars.ui.mypage.adapter.MedalRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyMealViewModel
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.loadUrlImg
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class MyMedalFragment : BaseFragment<FragmentMyMedalBinding, MyMealViewModel>(), OnBackPressedListener {

    override val viewModel: MyMealViewModel by viewModels()
    private val pageViewModel: MyPageViewModel by activityViewModels()


    private lateinit var adapter: MedalRecyclerAdapter

    override fun initView() {
        adapter = MedalRecyclerAdapter {
            viewModel.sendClickMedal(it.medal.medalId ?: -1)
            if (!it.isSelected && it.isExist) {
                showChangeMyMedalDialog(it.medal.medalId ?: -1)
            }
        }
        binding.rvMedal.adapter = adapter
        binding.rvMedal.itemAnimator = null
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            pageViewModel.getUserInfo()
        }
        binding.tvAllMedal.setOnClickListener {
            MedalInfoDialog().show(this.parentFragmentManager, MedalInfoDialog::class.java.simpleName)
        }
        observeData()
    }

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        pageViewModel.getUserInfo()
    }

    private fun observeData() {
        viewModel.myMedals.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.selectedMedal.observe(viewLifecycleOwner) {
            binding.ivMyMedal.loadUrlImg(it?.iconUrl)
            binding.tvMyMedal.text = buildSpannedString {
                append("현재 ")
                color(ContextCompat.getColor(requireContext(), DesignSystemR.color.color_sub_red)) {
                    append(it?.name)
                }
                append(" 장착 중")
            }
            viewModel.requestMyMedals()
        }
        viewModel.msgTextId.observe(viewLifecycleOwner) {
            if (it >= 0) {
                binding.root.showSnack(it)
            }
        }
    }

    private fun showChangeMyMedalDialog(medalId: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage(CommonR.string.change_medal)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(zion830.com.common.R.string.ok) { _, _ -> viewModel.updateMedal(medalId) }
            .create()
            .show()
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyMedalBinding =
        FragmentMyMedalBinding.inflate(inflater, container, false)
}