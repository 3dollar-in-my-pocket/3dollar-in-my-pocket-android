package com.zion830.threedollars.ui.ads

import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.zion830.threedollars.BuildConfig
import com.threedollar.common.R as CommonR

/**
 * 가게 AD_MOB 섹션 배너. 가게 상세와 홈 미리보기 시트가 함께 쓴다.
 * 슬롯 높이는 서버가 내려준 카드 높이를 따르고, 로드에 실패하면 여백까지 접어 빈 자리를 남기지 않는다.
 * 실패 상태는 카드별로 둔다. 같은 자리에 다른 가게 카드가 오면 다시 요청해야 하기 때문이다.
 *
 * @param adVisibility AdView 의 visibility. 미리 요청만 하고 노출은 막아야 하면 [View.INVISIBLE] 로 둔다 (TH-1465).
 */
@Composable
fun StoreAdMobBanner(
    card: SDStoreAdmobSectionModel.Card,
    onAdClick: (SDLogModel?) -> Unit,
    modifier: Modifier = Modifier,
    adVisibility: Int = View.VISIBLE,
) {
    var isFailed by remember(card) { mutableStateOf(false) }
    if (isFailed) return
    val context = LocalContext.current
    val currentCard by rememberUpdatedState(card)
    val currentOnAdClick by rememberUpdatedState(onAdClick)
    val fallbackAdUnitId = context.getString(CommonR.string.admob_store_detail_banner)
    SduiAdMobSlot(
        adUnitId = AdMobUnitId.resolve(
            serverUnitId = card.adUnitId,
            fallbackUnitId = fallbackAdUnitId,
            isDebug = BuildConfig.DEBUG,
        ),
        fallbackAdUnitId = fallbackAdUnitId,
        heightDp = card.slotHeightDp,
        adListener = remember(card) {
            object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isFailed = true
                }

                override fun onAdClicked() {
                    currentOnAdClick(currentCard.clickLog)
                }
            }
        },
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        adVisibility = adVisibility,
    )
}
