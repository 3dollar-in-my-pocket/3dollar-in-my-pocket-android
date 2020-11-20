package com.zion830.threedollars.ui.mypage

import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyStoreBinding
import com.zion830.threedollars.ui.mypage.adapter.MyStoreRecyclerAdapter
import zion830.com.common.base.BaseFragment

class MyStoreFragment : BaseFragment<FragmentMyStoreBinding, UserInfoViewModel>(R.layout.fragment_my_store) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val adapter = MyStoreRecyclerAdapter()

    override fun initView() {
        binding.rvStore.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myAllStore.observe(this) {
            adapter.submitList(it)
        }
    }
}