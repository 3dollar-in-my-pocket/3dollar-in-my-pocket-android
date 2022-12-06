package com.zion830.threedollars.ui.favorite

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFavoriteMyFolderBinding
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.mypage.adapter.MyStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

@AndroidEntryPoint
class FavoriteMyFolderActivity : BaseActivity<ActivityFavoriteMyFolderBinding, FavoriteViewModel>(R.layout.activity_favorite_my_folder) {
    override val viewModel: FavoriteViewModel by viewModels()

    private val adapter: FavoriteMyFolderRecyclerAdapter by lazy {
        FavoriteMyFolderRecyclerAdapter(object : OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel> {
            override fun onClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
                val intent = if (item.storeType == Constants.BOSS_STORE) {
                    FoodTruckStoreDetailActivity.getIntent(this@FavoriteMyFolderActivity, item.storeId)
                } else {
                    StoreDetailActivity.getIntent(this@FavoriteMyFolderActivity, item.storeId.toInt())
                }
                startActivity(intent)
            }
        }, object : OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel> {
            override fun onClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
                viewModel.deleteFavorite(item.storeType, item.storeId)
            }
        })
    }

    override fun initView() {
        viewModel.getMyFavoriteFolder()

        binding.favoriteListRecyclerView.adapter = adapter

        binding.backImageView.setOnClickListener {
            finish()
        }
        binding.deleteTextView.setOnClickListener {
            binding.deleteTextView.isVisible = false
            binding.doingDeleteLinearLayout.isVisible = true
            adapter.setDelete(true)
        }

        binding.deleteCompleteTextView.setOnClickListener {
            binding.deleteTextView.isVisible = true
            binding.doingDeleteLinearLayout.isVisible = false
            adapter.setDelete(false)
        }

        binding.allDeleteTextView.setOnClickListener {
            viewModel.allDeleteFavorite()
        }
        binding.infoEditTextView.setOnClickListener {
            startActivity(FavoriteMyInfoEditActivity.getIntent(this, binding.favoriteTitleTextView.text.toString(), binding.favoriteBodyTextView.text.toString()))
        }

        viewModel.isRefresh.observe(this) {
            if (it) {
                adapter.refresh()
            }
        }
        viewModel.myFavoriteFolderResponse.observe(this) {
            val title = it.name.ifEmpty { getString(R.string.favorite) }
            binding.favoriteTitleTextView.text = getString(R.string.favorite_title, it.user.name, title)
            binding.favoriteBodyTextView.text = it.introduction
        }
        lifecycleScope.launch {
            launch {
                viewModel.favoriteMyFolderPager.collectLatest {
                    adapter.submitData(it)
                }
            }
            launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    if (loadState.refresh is LoadState.NotLoading) {
                        val isEmpty = adapter.itemCount == 0

                        binding.itemCountTextView.text = getString(R.string.count_list, adapter.itemCount)
                        binding.favoriteListRecyclerView.isVisible = isEmpty.not()
                        binding.emptyLinearLayout.isVisible = isEmpty
                    }
                }
            }
        }
    }
    override fun onRestart() {
        super.onRestart()
        viewModel.getMyFavoriteFolder()
    }
}