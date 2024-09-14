package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.network.data.visit_history.MyVisitHistoryV2
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyVisitHistoryBinding
import com.zion830.threedollars.ui.mypage.adapter.MyVisitHistoryRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyPageViewModel
import com.zion830.threedollars.ui.mypage.viewModel.MyVisitHistoryViewModel
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyVisitHistoryFragment :
    BaseFragment<FragmentMyVisitHistoryBinding, UserInfoViewModel>(), OnBackPressedListener {

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
        myPageViewModel.requestUserActivity()
    }

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        viewModel.updateUserInfo()
    }

    override fun initView() {
        adapter = MyVisitHistoryRecyclerAdapter(object : OnItemClickListener<MyVisitHistoryV2> {
            override fun onClick(item: MyVisitHistoryV2) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.store.storeId?.toIntOrNull())
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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyVisitHistoryFragment", screenName = null)
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
                            when (loadState.refresh) {
                                is LoadState.NotLoading, is LoadState.Error -> {
                                    binding.ivEmpty.isVisible = adapter?.itemCount == 0
                                    binding.layoutNoData.root.isVisible = adapter?.itemCount == 0
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyVisitHistoryBinding =
        FragmentMyVisitHistoryBinding.inflate(inflater, container, false)
}