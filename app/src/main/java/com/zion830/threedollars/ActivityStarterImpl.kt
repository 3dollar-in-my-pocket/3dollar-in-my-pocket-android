package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.threedollar.common.listener.ActivityStarter
import com.zion830.threedollars.ui.favorite.FavoriteMyFolderActivity
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Activity
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import javax.inject.Inject

class ActivityStarterImpl @Inject constructor() : ActivityStarter {
    override fun startStoreDetailActivity(context: Context, storeId: Int?, startCertification: Boolean, deepLinkStoreId: String?) {
        context.startActivity(
            StoreDetailV2Activity.getIntent(
                context = context,
                storeId = storeId?.toLong(),
                storeType = USER_STORE,
                startCertification = startCertification,
                deepLinkStoreId = deepLinkStoreId,
            )
        )
    }

    override fun startBossDetailActivity(context: Context, storeId: String?, deepLinkStoreId: String?) {
        context.startActivity(
            StoreDetailV2Activity.getIntent(
                context = context,
                storeId = storeId?.toLongOrNull(),
                storeType = BOSS_STORE,
                deepLinkStoreId = deepLinkStoreId,
            )
        )
    }

    override fun startFavoriteActivity(context: Context) {
        context.startActivity(Intent(context, FavoriteMyFolderActivity::class.java))
    }

    override fun activityNavigateToMainActivityOnCloseIfNeeded(activity: Activity) {
        activity.navigateToMainActivityOnCloseIfNeeded()
    }

}
