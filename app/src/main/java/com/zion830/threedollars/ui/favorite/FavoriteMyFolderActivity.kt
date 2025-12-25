package com.zion830.threedollars.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.databinding.ActivityFavoriteMyFolderBinding
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.dialog.AllDeleteFavoriteDialog
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeEvents()
    }

    override fun initView() {
        setDarkSystemBars()
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
            viewModel.share()
        }
        binding.backImageView.onSingleClick {
            setResult(RESULT_OK)
            finish()
        }
        binding.deleteTextView.onSingleClick {
            binding.deleteTextView.isVisible = false
            binding.doingDeleteLinearLayout.isVisible = true
            adapter.setDelete(true)
        }

        binding.deleteCompleteTextView.onSingleClick {
            binding.deleteTextView.isVisible = true
            binding.doingDeleteLinearLayout.isVisible = false
            adapter.setDelete(false)
        }

        binding.allDeleteTextView.onSingleClick {
            val dialog = AllDeleteFavoriteDialog()
            dialog.setDialogListener(object : AllDeleteFavoriteDialog.DialogListener {
                override fun click() {
                    viewModel.allDeleteFavorite()
                }
            })
            dialog.show(supportFragmentManager, dialog.tag)
        }
        binding.infoEditTextView.onSingleClick {
            activityResultLauncher.launch(
                FavoriteMyInfoEditActivity.getIntent(
                    this,
                    binding.favoriteTitleTextView.text.toString(),
                    binding.favoriteBodyTextView.text.toString()
                )
            )
        }
        viewModel.isRefresh.observe(this) {
            if (it) {
                adapter.refresh()
            }
        }
        viewModel.myFavoriteFolderResponse.observe(this) {
            val title = it.name.ifEmpty { getString(CommonR.string.favorite_title, it.user.name, getString(CommonR.string.favorite)) }
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

                        binding.itemCountTextView.text = getString(CommonR.string.count_list, adapter.itemCount)
                        binding.favoriteListRecyclerView.isVisible = isEmpty.not()
                        binding.emptyLinearLayout.isVisible = isEmpty
                    }
                }
            }
        }
    }

    private fun subscribeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect(::onEvent)
            }
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "FavoriteMyFolderActivity", screenName = null)
    }

    private fun onEvent(event: FavoriteViewModel.Event) {
        when (event) {
            is FavoriteViewModel.Event.Share -> {
                onShare(event)
            }
        }
    }

    private fun onShare(event: FavoriteViewModel.Event.Share) {
        val shareUri = getString(R.string.dynamic_link_bookmark_folder)
            .toUri()
            .buildUpon()
            .appendQueryParameter(SHARE_QUERY, event.folderId)
            .build()
            .toString()

        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareUri)
        }.let {
            startActivity(
                Intent.createChooser(it, "가슴속삼천원 즐겨찾기 공유하기")
            )
        }
    }

    companion object {
        private const val SHARE_QUERY = "folderId"
    }
}
