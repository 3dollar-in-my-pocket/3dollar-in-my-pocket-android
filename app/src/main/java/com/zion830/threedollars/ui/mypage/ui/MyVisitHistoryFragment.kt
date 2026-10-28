package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.R as CommonR
import com.threedollar.network.data.visit_history.MyVisitHistoryV2
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyVisitHistoryBinding
import com.zion830.threedollars.ui.mypage.adapter.MyVisitHistoryRecyclerAdapter
import com.zion830.threedollars.ui.mypage.model.MyVisitDeletePolicy
import com.zion830.threedollars.ui.mypage.model.MyVisitDeleteResult
import com.zion830.threedollars.ui.mypage.viewModel.MyVisitHistoryViewModel
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiActivity
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

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
        myPageViewModel.getUserInfo()
    }

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        viewModel.updateUserInfo()
    }

    override fun initView() {
        adapter = MyVisitHistoryRecyclerAdapter(
            listener = object : OnItemClickListener<MyVisitHistoryV2> {
                override fun onClick(item: MyVisitHistoryV2) {
                    sendClickStore(item.store.storeId.orEmpty(), item.store.storeType.orEmpty())
                    val storeId = item.store.storeId?.takeIf { it.isNotBlank() } ?: return
                    val intent = StoreDetailSduiActivity.getIntent(requireContext(), storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
                }
            },
            deleteListener = object : OnItemClickListener<MyVisitHistoryV2> {
                override fun onClick(item: MyVisitHistoryV2) {
                    showDeleteConfirmDialog(item)
                }
            },
            isDeletable = { item -> MyVisitDeletePolicy.isDeletable(item.visit.createdAt) },
        )
        binding.rvVisitHistory.adapter = adapter
        binding.tvEdit.onSingleClick { myVisitHistoryViewModel.toggleEditMode() }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        observeUiData()
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.VISITED_LIST, this::class.java.simpleName)
    }

    private fun sendClickStore(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.VISITED_LIST,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
            )
        )
    }

    private fun showDeleteConfirmDialog(item: MyVisitHistoryV2) {
        AlertDialog.Builder(requireContext())
            .setMessage(CommonR.string.my_visit_history_delete_confirm)
            .setPositiveButton(CommonR.string.delete) { _, _ -> myVisitHistoryViewModel.deleteVisit(item) }
            .setNegativeButton(CommonR.string.cancel, null)
            .show()
    }

    private fun handleDeleteResult(result: MyVisitDeleteResult) {
        when (result) {
            MyVisitDeleteResult.Deleted -> {
                showToast(CommonR.string.my_visit_history_deleted)
                adapter?.refresh()
                myPageViewModel.getUserInfo()
                myPageViewModel.getMyVisitsStore()
            }

            is MyVisitDeleteResult.Failed -> showToast(result.message ?: getString(CommonR.string.connection_failed))
        }
    }

    private fun updateEmptyState() {
        binding.emptyStateView.isVisible = adapter?.itemCount == 0
        val hasDeletable = MyVisitDeletePolicy.hasDeletable(adapter?.snapshot()?.items.orEmpty().map { it.visit.createdAt })
        binding.tvEdit.isVisible = hasDeletable
        if (!hasDeletable) myVisitHistoryViewModel.exitEditMode()
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
                                is LoadState.NotLoading, is LoadState.Error -> updateEmptyState()

                                else -> {}
                            }
                        }
                    }
                    launch {
                        myVisitHistoryViewModel.isEditMode.collect { isEditMode ->
                            adapter?.setEditMode(isEditMode)
                            binding.tvEdit.setText(if (isEditMode) CommonR.string.complete else CommonR.string.my_visit_history_edit)
                        }
                    }
                    launch {
                        myVisitHistoryViewModel.deleteResult.collect(::handleDeleteResult)
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyVisitHistoryBinding =
        FragmentMyVisitHistoryBinding.inflate(inflater, container, false)
}