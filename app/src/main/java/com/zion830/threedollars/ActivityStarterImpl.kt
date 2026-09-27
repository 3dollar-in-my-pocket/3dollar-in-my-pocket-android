package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.threedollar.common.listener.ActivityStarter
import com.zion830.threedollars.ui.favorite.FavoriteMyFolderActivity
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import javax.inject.Inject

class ActivityStarterImpl @Inject constructor() : ActivityStarter {
    override fun startStoreDetailActivity(context: Context, storeId: Int?, startCertification: Boolean, deepLinkStoreId: String?) {
        val certificationStoreId = (storeId ?: deepLinkStoreId?.toIntOrNull())?.takeIf { startCertification }
        if (certificationStoreId != null) {
            context.startActivity(StoreCertificationActivity.getIntent(context, certificationStoreId))
            return
        }
        startStoreDetailSduiActivity(context, storeId?.toString() ?: deepLinkStoreId)
    }

    override fun startBossDetailActivity(context: Context, storeId: String?, deepLinkStoreId: String?) {
        startStoreDetailSduiActivity(context, storeId ?: deepLinkStoreId)
    }

    private fun startStoreDetailSduiActivity(context: Context, storeId: String?) {
        if (storeId.isNullOrBlank()) return
        context.startActivity(StoreDetailSduiActivity.getIntent(context, storeId))
    }

    override fun startFavoriteActivity(context: Context) {
        context.startActivity(Intent(context, FavoriteMyFolderActivity::class.java))
    }

    override fun activityNavigateToMainActivityOnCloseIfNeeded(activity: Activity) {
        activity.navigateToMainActivityOnCloseIfNeeded()
    }

}