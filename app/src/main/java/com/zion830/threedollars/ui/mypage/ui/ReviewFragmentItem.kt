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
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.network.data.user.MyReviewResponseData
import com.zion830.threedollars.databinding.FragmentReviewItemBinding
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyReviewViewModel
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
                val intent = StoreDetailActivity.getIntent(requireContext(), item.store.storeId?.toIntOrNull())
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                    myReviewViewModel.myReviewPager.collectLatest {
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

