package com.zion830.threedollars.ui.favorite

import androidx.activity.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFavoriteMyFolderBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseActivity

@AndroidEntryPoint
class FavoriteMyFolderActivity : BaseActivity<ActivityFavoriteMyFolderBinding, FavoriteViewModel>(R.layout.activity_favorite_my_folder) {
    override val viewModel: FavoriteViewModel by viewModels()

    override fun initView() {

    }

}