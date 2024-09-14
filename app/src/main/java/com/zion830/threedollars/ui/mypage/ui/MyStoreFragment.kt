package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.network.data.store.MyReportedContent
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyStoreBinding
import com.zion830.threedollars.ui.mypage.adapter.MyStoreRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyPageViewModel
import com.zion830.threedollars.ui.mypage.viewModel.MyStoreViewModel
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyStoreFragment :
    BaseFragment<FragmentMyStoreBinding, UserInfoViewModel>(), OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private val myStoreViewModel: MyStoreViewModel by viewModels()

    private var adapter: MyStoreRecyclerAdapter? = null

    override fun onResume() {
        super.onResume()
        adapter?.refresh()
    }

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        viewModel.updateUserInfo()
    }

    override fun initView() {
        adapter = MyStoreRecyclerAdapter(object : OnItemClickListener<MyReportedContent> {
            override fun onClick(item: MyReportedContent) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.store?.storeId)
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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyStoreFragment", screenName = null)
    }

    override fun onStop() {
        super.onStop()
        myPageViewModel.requestUserActivity()
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
                        when (loadState.refresh) {
                            is LoadState.NotLoading, is LoadState.Error -> {
                                binding.ivEmpty.isVisible = adapter?.itemCount == 0
                                binding.layoutNoData.root.isVisible = adapter?.itemCount == 0
                                binding.tvStoreCount.isVisible = adapter?.itemCount ?: 0 > 0
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyStoreBinding =
        FragmentMyStoreBinding.inflate(inflater, container, false)
}