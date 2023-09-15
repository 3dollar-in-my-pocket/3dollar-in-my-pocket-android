package com.zion830.threedollars.ui.mypage

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.BOSS_STORE
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentNewMyPageBinding
import com.zion830.threedollars.datasource.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.ui.MyPageSettingFragment
import com.zion830.threedollars.ui.favorite.FavoriteMyFolderActivity
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.mypage.adapter.MyPageRecyclerAdapter
import com.zion830.threedollars.ui.mypage.vm.MyPageViewModel
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener

@AndroidEntryPoint
class MyPageFragment : LegacyBaseFragment<FragmentNewMyPageBinding, MyPageViewModel>(R.layout.fragment_new_my_page) {

    override val viewModel: MyPageViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    private lateinit var visitHistoryAdapter: MyPageRecyclerAdapter

    private lateinit var myFavoriteAdapter: MyPageRecyclerAdapter

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        viewModel.requestUserActivity()
        viewModel.requestVisitHistory()
        userInfoViewModel.updateUserInfo()
    }

    override fun initView() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                viewModel.getMyFavoriteFolder()
            }
        }
        viewModel.initAllMedals()
        viewModel.getMyFavoriteFolder()
        visitHistoryAdapter = MyPageRecyclerAdapter(
            object : OnItemClickListener<AdAndStoreItem> {
                override fun onClick(item: AdAndStoreItem) {
                    val visitHistoryContent = item as VisitHistoryContent
                    val intent = StoreDetailActivity.getIntent(requireContext(), visitHistoryContent.store.storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
                }
            })

        myFavoriteAdapter = MyPageRecyclerAdapter(object : OnItemClickListener<AdAndStoreItem> {
            override fun onClick(item: AdAndStoreItem) {
                val myFavoriteFolderFavoriteModel = item as MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel
                if (item.storeType == BOSS_STORE) {
                    activityResultLauncher.launch(FoodTruckStoreDetailActivity.getIntent(requireContext(), myFavoriteFolderFavoriteModel.storeId))
                } else {
                    activityResultLauncher.launch(StoreDetailActivity.getIntent(requireContext(), myFavoriteFolderFavoriteModel.storeId.toInt()))
                }
            }

        })

        binding.rvRecentVisitHistory.adapter = visitHistoryAdapter
        binding.favoriteRecyclerView.adapter = myFavoriteAdapter
        LinearSnapHelper().attachToRecyclerView(binding.rvRecentVisitHistory)
        LinearSnapHelper().attachToRecyclerView(binding.favoriteRecyclerView)

        binding.ibSetting.onSingleClick {
            addSettingPageFragment()
        }
        binding.layoutStore.onSingleClick {
            addShowAllStoreFragment()
        }
        binding.layoutReview.onSingleClick {
            addShowAllReviewFragment()
        }
        binding.layoutMedal.onSingleClick {
            addShowAllMedalFragment()
        }
        binding.tvMessage.setOnClickListener {
            addShowAllVisitHistoryFragment()
        }
        binding.ivProfile.setOnClickListener {
            addShowAllMedalFragment()
        }
        binding.tvName.setOnClickListener {
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                EditNameFragment(),
                EditNameFragment::class.java.name
            )
        }
        binding.favoriteMoreTextView.setOnClickListener {
            activityResultLauncher.launch(Intent(requireActivity(), FavoriteMyFolderActivity::class.java))
        }
        observeUiData()
        if (viewModel.isMoveMedalPage) {
            addShowAllMedalFragment()
        }
    }

    private fun observeUiData() {
        viewModel.myVisitHistory.observe(viewLifecycleOwner) {
            val emptyResponseList = listOf(
                StoreEmptyResponse(
                    emptyImage = R.drawable.img_empty,
                    emptyTitle = R.string.no_visit_history,
                    emptyBody = R.string.no_visit_history_msg
                )
            )
            if (it.isNullOrEmpty()) {
                visitHistoryAdapter.submitList(emptyResponseList)
            } else {
                visitHistoryAdapter.submitList(it)
            }
        }
        userInfoViewModel.userInfo.observe(viewLifecycleOwner) {
            binding.tvName.text = it.data.name
        }
        viewModel.selectedMedal.observe(viewLifecycleOwner) {
            binding.tvUserMedal.text = it?.name ?: "장착한 칭호가 없어요!"
            binding.ivProfile.loadUrlImg(it?.iconUrl)
        }
        viewModel.myFavoriteModel.observe(viewLifecycleOwner) {
            val emptyResponseList = listOf(
                StoreEmptyResponse(
                    emptyImage = R.drawable.img_empty,
                    emptyTitle = R.string.mypage_favorite_empty_title,
                    emptyBody = R.string.mypage_favorite_empty_body
                )
            )
            if (it.isNullOrEmpty()) {
                myFavoriteAdapter.submitList(emptyResponseList)
            } else {
                myFavoriteAdapter.submitList(it)
            }
        }
    }

    private fun addSettingPageFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyPageSettingFragment(),
            MyPageSettingFragment::class.java.name
        )
    }

    private fun addShowAllStoreFragment() {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_STORE_BTN_CLICKED)
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyStoreFragment(),
            MyStoreFragment::class.java.name
        )
    }

    private fun addShowAllReviewFragment() {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_REVIEW_BTN_CLICKED)
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyReviewFragment(),
            MyReviewFragment::class.java.name
        )
    }

    private fun addShowAllMedalFragment() {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_MEDAL_BTN_CLICKED)
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyMedalFragment(),
            MyMedalFragment::class.java.name
        )
    }

    private fun addShowAllVisitHistoryFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyVisitHistoryFragment(),
            MyVisitHistoryFragment::class.java.name
        )
    }
}