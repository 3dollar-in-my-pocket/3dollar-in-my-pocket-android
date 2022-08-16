package com.zion830.threedollars


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity

enum class DeepLinkInfo(@StringRes val hostStringResId: Int) {

    MAIN(R.string.scheme_host_main) {
        override fun getIntent(context: Context, deepLinkUri: Uri) =
            getMainIntent(context)
    },

    DETAIL(R.string.scheme_host_kakao_link) {
        override fun getIntent(context: Context, deepLinkUri: Uri): Intent {
            return if (deepLinkUri.getQueryParameter(context.getString(R.string.scheme_host_kakao_link_store_type)) ==
                context.getString(R.string.scheme_host_kakao_link_food_truck_type)
            ) FoodTruckStoreDetailActivity.getIntent(context, deepLink = deepLinkUri)
            else {
                StoreDetailActivity.getIntent(context, deepLink = deepLinkUri)
            }
        }
    };

    private val host: String = GlobalApplication.instance.getString(hostStringResId)

    abstract fun getIntent(context: Context, deepLinkUri: Uri): Intent

    companion object {

        fun getMainIntent(context: Context) = MainActivity.getIntent(context)

        operator fun invoke(uri: Uri): DeepLinkInfo =
            values().find { it.host == uri.host } ?: run {
                Log.e("deepLink", "등록되지 않은 딥링크")
                Log.e("deepLink", "Not registered deep link host")
                MAIN
            }
    }
}