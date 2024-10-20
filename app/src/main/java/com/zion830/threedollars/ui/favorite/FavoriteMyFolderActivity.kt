package com.zion830.threedollars.ui.favorite

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFavoriteMyFolderBinding
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.ui.dialog.AllDeleteFavoriteDialog
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class FavoriteMyFolderActivity : BaseActivity<ActivityFavoriteMyFolderBinding, FavoriteViewModel>({ ActivityFavoriteMyFolderBinding.inflate(it) }) {
    override val viewModel: FavoriteViewModel by viewModels()

    private val adapter: FavoriteMyFolderRecyclerAdapter by lazy {
        FavoriteMyFolderRecyclerAdapter(object : OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel> {
            override fun onClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
                if (item.storeType == Constants.BOSS_STORE) {
                    activityResultLauncher.launch(BossStoreDetailActivity.getIntent(this@FavoriteMyFolderActivity, item.storeId))
                } else {
                    activityResultLauncher.launch(StoreDetailActivity.getIntent(this@FavoriteMyFolderActivity, item.storeId.toInt()))
                }
            }
        }, object : OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel> {
            override fun onClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
                viewModel.deleteFavorite(item.storeId)
            }
        })
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getMyFavoriteFolder()
                adapter.refresh()
            }
        }

        viewModel.getMyFavoriteFolder()

        binding.favoriteListRecyclerView.adapter = adapter
        binding.shareImageView.onSingleClick {
            viewModel.createShareUrl()
        }
        binding.backImageView.setOnClickListener {
            setResult(RESULT_OK)
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
            val dialog = AllDeleteFavoriteDialog()
            dialog.setDialogListener(object : AllDeleteFavoriteDialog.DialogListener {
                override fun click() {
                    viewModel.allDeleteFavorite()
                }
            })
            dialog.show(supportFragmentManager, dialog.tag)
        }
        binding.infoEditTextView.setOnClickListener {
            activityResultLauncher.launch(
                FavoriteMyInfoEditActivity.getIntent(
                    this,
                    binding.favoriteTitleTextView.text.toString(),
                    binding.favoriteBodyTextView.text.toString()
                )
            )
        }
        viewModel.dynamicLink.observe(this) {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, it)
            }, "가슴속삼천원 즐겨찾기 공유하기"))
        }
        viewModel.isRefresh.observe(this) {
            if (it) {
                adapter.refresh()
            }
        }
        viewModel.myFavoriteFolderResponse.observe(this) {
            val title = it.name.ifEmpty { getString(R.string.favorite_title, it.user.name, getString(R.string.favorite)) }
            binding.favoriteTitleTextView.text = title
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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "FavoriteMyFolderActivity", screenName = null)
    }
}