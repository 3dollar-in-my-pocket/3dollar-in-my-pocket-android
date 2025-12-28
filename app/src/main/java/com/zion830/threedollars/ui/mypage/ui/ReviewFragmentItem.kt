package com.zion830.threedollars.ui.mypage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.network.data.user.MyReviewResponseData
import com.zion830.threedollars.databinding.FragmentReviewItemBinding
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyReviewViewModel
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReviewFragmentItem : Fragment() {

    private var adapter: MyReviewRecyclerAdapter? = null
    private val myReviewViewModel: MyReviewViewModel by activityViewModels()
    private lateinit var binding: FragmentReviewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MyReviewRecyclerAdapter(object : OnItemClickListener<MyReviewResponseData> {
            override fun onClick(item: MyReviewResponseData) {
                sendClickReview(item.store.storeId.orEmpty(), item.store.storeType.orEmpty())
                if (item.store.storeType == BOSS_STORE) {
                    val intent = BossStoreDetailActivity.getIntent(
                        requireContext(),
                        item.store.storeId.toString()
                    )
                    startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
                } else {
                    val intent = StoreDetailActivity.getIntent(
                        requireContext(),
                        item.store.storeId?.toIntOrNull()
                    )
                    startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiData()
        binding.recycler.adapter = adapter
    }

    private fun observeUiData() {
        myReviewViewModel.updateReview.observe(viewLifecycleOwner) {
            adapter?.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myReviewViewModel.userStoreReviewPager.collectLatest {
                        adapter?.submitData(it)
                    }
                }
                launch {
                    adapter?.loadStateFlow?.collectLatest { loadState ->
                        if (loadState.refresh is LoadState.NotLoading) {
                            val isEmpty = adapter?.itemCount == 0

                            binding.ivEmpty.isVisible = isEmpty
                            binding.emptyStateView.isVisible = isEmpty
                        }
                    }
                }
            }
        }
    }

    private fun sendClickReview(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.MY_REVIEW,
                objectType = LogObjectType.REVIEW,
                objectId = LogObjectId.REVIEW,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
            )
        )
    }

}

