package com.zion830.threedollars.ui.favorite.viewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.loadImage
import com.threedollar.common.ext.toStringDefault
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.databinding.ActivityFavoriteViewerBinding
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.ui.dialog.LoginRequestDialog
import com.zion830.threedollars.ui.login.ui.SignUpActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import com.zion830.threedollars.utils.requestPermissionFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class FavoriteViewerActivity : BaseActivity<ActivityFavoriteViewerBinding, FavoriteViewerViewModel>({ ActivityFavoriteViewerBinding.inflate(it) }) {

    override val viewModel: FavoriteViewerViewModel by viewModels()
    private lateinit var favoriteId: String
    private val adapter by lazy {
        FavoriteViewerAdapter(viewModel)
    }
    private var selectedItem: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel? = null
    private val inputNameLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedItem?.run {
                moveToDetailActivity(this)
            }
            selectedItem = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteId = intent.getStringExtra("favoriteId").toStringDefault()
        if (!this::favoriteId.isInitialized || favoriteId.isEmpty()) {
            return finish()
        }
        binding.favoriteListRecyclerView.adapter = adapter
        viewModel.getFavoriteViewer(favoriteId)
    }

    override fun initView() {
        setDarkSystemBars()
        requestPermissionFirst()
        initObserve()
        initButton()
    }

    private fun initButton() {
        binding.closeImage.onSingleClick {
            viewModel.onEventClick(FavoriteViewerViewModel.Event.Close)
        }
    }

    private fun initObserve() {
        viewModel.eventClick.observe(this) {
            when (it) {
                FavoriteViewerViewModel.Event.Close -> finish()
                is FavoriteViewerViewModel.Event.Viewer -> {
                    val item = it.item
                    viewModel.sendClickStore(item.storeId, item.storeType)
                    if (GlobalApplication.isLoggedIn) {
                        moveToDetailActivity(item)
                    } else {
                        LoginRequestDialog().setLoginCallBack { isNameUpdate ->
                            if (isNameUpdate) {
                                moveToDetailActivity(item)
                            } else {
                                selectedItem = item
                                inputNameLauncher.launch(Intent(this, SignUpActivity::class.java))
                            }
                        }.show(supportFragmentManager, "")
                    }
                }
            }
        }
        viewModel.favoriteViewer.observe(this) {
            adapter.submitList(it.favorites)
            binding.favoriteCountText.text = getString(CommonR.string.count_list, adapter.itemCount)
            binding.favoriteTitleText.textFavoriteTitle(it)
            binding.favoriteUserMedalText.text = it.user.medal.name
            binding.favoriteUserMedalImage.loadImage(it.user.medal.iconUrl)
            binding.favoriteUserNameText.textFavoriteUserName(it.user.name)
            binding.favoriteMemoText.text = it.introduction
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.error.collect(::onError)
            }
        }
    }

    private fun onError(error: FavoriteViewerError) {
        val title = when (error) {
            is FavoriteViewerError.ApiError -> {
                error.message.takeIf { it.isNotEmpty() } ?: getString(CommonR.string.error_connection_failed_title)
            }

            is FavoriteViewerError.Unknown -> {
                getString(CommonR.string.error_connection_failed_title)
            }
        }
        val message = when (error) {
            is FavoriteViewerError.ApiError -> {
                null
            }

            is FavoriteViewerError.Unknown -> {
                getString(CommonR.string.error_connection_failed_message)
            }
        }

        AlertDialog.Builder(this)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .create()
            .show()
    }

    override fun finish() {
        navigateToMainActivityOnCloseIfNeeded()
        super.finish()
    }

    private fun moveToDetailActivity(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
        val intent = if (item.storeType == Constants.BOSS_STORE) {
            BossStoreDetailActivity.getIntent(this@FavoriteViewerActivity, item.storeId)
        } else {
            StoreDetailActivity.getIntent(this@FavoriteViewerActivity, item.storeId.toInt())
        }
        startActivity(intent)
    }
}
