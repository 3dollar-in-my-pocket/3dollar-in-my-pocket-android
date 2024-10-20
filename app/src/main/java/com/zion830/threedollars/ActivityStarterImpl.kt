package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.threedollar.common.listener.ActivityStarter
import com.zion830.threedollars.ui.favorite.FavoriteMyFolderActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import javax.inject.Inject

class ActivityStarterImpl @Inject constructor() : ActivityStarter {
    override fun startStoreDetailActivity(context: Context, storeId: Int?, startCertification: Boolean, deepLinkStoreId: String?) {
        context.startActivity(StoreDetailActivity.getIntent(context, storeId, startCertification, deepLinkStoreId))
    }

    override fun startBossDetailActivity(context: Context, storeId: String?, deepLinkStoreId: String?) {
        context.startActivity(BossStoreDetailActivity.getIntent(context, storeId, deepLinkStoreId))
    }

    override fun startFavoriteActivity(context: Context) {
        context.startActivity(Intent(context, FavoriteMyFolderActivity::class.java))
    }

    override fun activityNavigateToMainActivityOnCloseIfNeeded(activity: Activity) {
        activity.navigateToMainActivityOnCloseIfNeeded()
    }

}