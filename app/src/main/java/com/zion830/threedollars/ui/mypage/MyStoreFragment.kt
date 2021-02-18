package com.zion830.threedollars.ui.mypage

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyStoreBinding
import com.zion830.threedollars.repository.model.response.Store
import com.zion830.threedollars.ui.mypage.adapter.MyStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class MyStoreFragment : BaseFragment<FragmentMyStoreBinding, UserInfoViewModel>(R.layout.fragment_my_store) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private lateinit var adapter: MyStoreRecyclerAdapter

    override fun initView() {
        adapter = MyStoreRecyclerAdapter(object : OnItemClickListener<Store> {
            override fun onClick(item: Store) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.id)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        })
        binding.rvStore.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myAllStore.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.SHOW_STORE_DETAIL -> {
                // viewModel.updatePreviewData()
                // TODO : 강제 갱신되도록 처리
            }
        }
    }
}