package com.zion830.threedollars.ui.mypage

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
import com.zion830.threedollars.databinding.FragmentMyVisitHistoryBinding
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.ui.mypage.adapter.MyVisitHistoryRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class MyVisitHistoryFragment :
    BaseFragment<FragmentMyVisitHistoryBinding, UserInfoViewModel>(R.layout.fragment_my_visit_history) {

    override val viewModel: UserInfoViewModel by activityViewModels()
    private val myVisitHistoryViewModel: MyVisitHistoryViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private var adapter: MyVisitHistoryRecyclerAdapter? = null

    override fun onResume() {
        super.onResume()
        adapter?.refresh()
    }

    override fun onStart() {
        super.onStart()
        myPageViewModel.requestVisitHistory()
        myPageViewModel.requestUserActivity()
    }

    override fun initView() {
        adapter = MyVisitHistoryRecyclerAdapter(object : OnItemClickListener<VisitHistoryContent> {
            override fun onClick(item: VisitHistoryContent) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.store.storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        })
        binding.rvVisitHistory.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        observeUiData()
    }

    private fun observeUiData() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        myVisitHistoryViewModel.myHistoryPager.collectLatest {
                            adapter?.submitData(it)
                        }
                    }
                    launch {
                        adapter?.loadStateFlow?.collectLatest { loadState ->
                            if (loadState.refresh is LoadState.NotLoading) {
                                binding.ivEmpty.isVisible = adapter?.itemCount == 0
                                binding.layoutNoData.root.isVisible = adapter?.itemCount == 0
                            }
                        }
                    }
                }
            }
        }
    }
}