package com.zion830.threedollars.ui.favorite.viewer

import android.os.Bundle
import androidx.activity.viewModels
import com.zion830.threedollars.BR
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFavoriteViewerBinding
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.toStringDefault

@AndroidEntryPoint
class FavoriteViewerActivity : BaseActivity<ActivityFavoriteViewerBinding, FavoriteViewerViewModel>(R.layout.activity_favorite_viewer) {
    override val viewModel: FavoriteViewerViewModel by viewModels()
    private lateinit var favoriteId: String
    private val adapter by lazy {
        FavoriteViewerAdapter(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.setVariable(BR.item, MyFavoriteFolderResponse())
        favoriteId = intent.getStringExtra("favoriteId").toStringDefault()
        if (!this::favoriteId.isInitialized || favoriteId.isEmpty()) {
            return finish()
        }
        binding.favoriteListRecyclerView.adapter = adapter
        viewModel.getFavoriteViewer(favoriteId)
    }

    override fun initBinding() {
        super.initBinding()
        viewModel.eventClick.observe(this) {
            when (it) {
                FavoriteViewerViewModel.Event.Close -> finish()
                is FavoriteViewerViewModel.Event.Viewer -> {}
            }
        }
        viewModel.favoriteViewer.observe(this) {
            adapter.submitList(it.favorites)
            binding.favoriteCountText.text = getString(R.string.count_list, adapter.itemCount)
            binding.setVariable(BR.item, it)
        }
    }

    override fun initView() {

    }

}