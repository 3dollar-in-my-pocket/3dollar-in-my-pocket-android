package com.zion830.threedollars.ui.mypage

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyStoreBinding
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.MyPageSettingFragment
import com.zion830.threedollars.ui.mypage.adapter.MyStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener

class MyStoreFragment :
    BaseFragment<FragmentMyStoreBinding, UserInfoViewModel>(R.layout.fragment_my_store) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private val myStoreViewModel: MyStoreViewModel by viewModels()

    private var adapter: MyStoreRecyclerAdapter? = null

    override fun onResume() {
        super.onResume()
        adapter?.refresh()
    }

    override fun initView() {
        adapter = MyStoreRecyclerAdapter(object : OnItemClickListener<StoreInfo> {
            override fun onClick(item: StoreInfo) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        })
        binding.rvStore.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        binding.ibSetting.setOnClickListener {
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                MyPageSettingFragment(),
                MyPageSettingFragment::class.java.name
            )
        }
        observeUiData()
    }

    override fun onStop() {
        super.onStop()
        myPageViewModel.requestUserActivity()
        myPageViewModel.requestVisitHistory()
    }

    private fun observeUiData() {
        myStoreViewModel.totalCount.observe(viewLifecycleOwner) {
            binding.tvStoreCount.text = buildSpannedString {
                bold {
                    append("${it}개")
                }
                append(" 제보하셨네요!")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myStoreViewModel.myStorePager.collectLatest {
                        adapter?.submitData(it)
                    }
                }
                launch {
                    adapter?.loadStateFlow?.collectLatest { loadState ->
                        if (loadState.refresh is LoadState.NotLoading) {
                            binding.ivEmpty.isVisible = adapter?.itemCount == 0
                            binding.layoutNoData.root.isVisible = adapter?.itemCount == 0
                            binding.tvStoreCount.isVisible = adapter?.itemCount ?: 0 > 0
                        }
                    }
                }
            }
        }
    }
}