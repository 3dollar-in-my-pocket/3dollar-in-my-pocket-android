package com.zion830.threedollars.ui.storeDetail.sdui.model

import android.view.View

/**
 * 홈 시트 tip 에서 미리 그려 둔 가게 상세가 광고·임베드를 언제 로드하고 보여도 되는지 정한다. Compose 에 의존하지 않도록 값만 받아 계산한다.
 *
 * - 애드몹 배너: 미리보기 상태에서도 바로 요청해 올렸을 때 이미 떠 있게 하고, AdView 는 상세가 보일 때만 VISIBLE 로 둔다([bannerVisibility]).
 *   미리보기에 가려진 채 VISIBLE 이면 SDK 가 보인다고 판단해 사용자가 보지 않은 광고가 노출로 집계된다 (TH-1465).
 * - 쓰레드 임베드: 상세가 보인 뒤에만 로드한다([initial], [next]).
 */
object StoreDetailAdLoadPolicy {

    /**
     * 가게(또는 화면)를 새로 그리기 시작할 때 임베드를 로드해도 되는지.
     * 독립 가게 상세 화면은 처음부터 보이므로 바로 로드하고, 홈 시트 tip 에서는 보이기 전까지 막는다.
     */
    fun initial(isDisplayed: Boolean): Boolean = isDisplayed

    /**
     * 표시 상태가 바뀔 때의 값.
     * 한 번 보인 뒤에는 같은 가게 안에서 tip 으로 내려도 유지한다. 내렸다 올릴 때마다 새로 로드하지 않기 위해서다.
     */
    fun next(canLoad: Boolean, isDisplayed: Boolean): Boolean = canLoad || isDisplayed

    /** 배너 AdView 의 visibility. 상세가 지금 보일 때만 VISIBLE 이라 미리보기·tip 상태에서는 노출이 잡히지 않는다. */
    fun bannerVisibility(isDisplayed: Boolean): Int = if (isDisplayed) View.VISIBLE else View.INVISIBLE
}
