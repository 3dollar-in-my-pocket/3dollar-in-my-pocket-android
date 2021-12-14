package com.zion830.threedollars.ui.mypage

import android.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentMyMedalBinding
import com.zion830.threedollars.ui.mypage.adapter.MedalRecyclerAdapter
import com.zion830.threedollars.ui.mypage.ui.MedalInfoDialog
import com.zion830.threedollars.ui.mypage.vm.MyPageViewModel
import zion830.com.common.base.BaseFragment

class MyMedalFragment : BaseFragment<FragmentMyMedalBinding, MyPageViewModel>(R.layout.fragment_my_medal) {

    override val viewModel: MyPageViewModel by activityViewModels()

    private lateinit var adapter: MedalRecyclerAdapter

    override fun initView() {
        adapter = MedalRecyclerAdapter {
            if (!it.isSelected && it.isExist) {
                showChangeMyMedalDialog(it.medal.medalId ?: -1)
            }
        }
        binding.rvMedal.adapter = adapter
        binding.rvMedal.itemAnimator = null
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.tvAllMedal.setOnClickListener {
            MedalInfoDialog().show(this.parentFragmentManager, MedalInfoDialog::class.java.simpleName)
        }
        observeData()
    }

    private fun observeData() {
        viewModel.myMedals.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.selectedMedal.observe(viewLifecycleOwner) {
            binding.ivMyMedal.loadUrlImg(it?.iconUrl)
            binding.tvMyMedal.text = buildSpannedString {
                append("현재 ")
                color(ContextCompat.getColor(requireContext(), R.color.color_sub_red)) {
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
            .setMessage(R.string.change_medal)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.ok) { _, _ -> viewModel.changeMedal(medalId) }
            .create()
            .show()
    }
}