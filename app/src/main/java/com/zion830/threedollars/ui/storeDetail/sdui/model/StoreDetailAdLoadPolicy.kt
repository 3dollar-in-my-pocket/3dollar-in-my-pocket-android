package com.zion830.threedollars.ui.storeDetail.sdui.model

/**
 * 가게 상세 애드몹 배너를 언제 로드해도 되는지 정한다. Compose 에 의존하지 않도록 값만 받아 계산한다.
 *
 * 홈 시트는 미리보기(tip) 상태에서도 올릴 때 바로 보이도록 가게 상세를 뒤에 미리 그려 둔다.
 * 이때 광고까지 로드하면 사용자가 보지 않은 광고가 노출로 집계되므로, 상세가 실제로 보인 뒤에만 로드한다.
 */
object StoreDetailAdLoadPolicy {

    /**
     * 가게(또는 화면)를 새로 그리기 시작할 때의 값.
     * 독립 가게 상세 화면은 처음부터 보이므로 바로 로드하고, 홈 시트 tip 에서는 보이기 전까지 막는다.
     */
    fun initial(isDisplayed: Boolean): Boolean = isDisplayed

    /**
     * 표시 상태가 바뀔 때의 값.
     * 한 번 보인 뒤에는 같은 가게 안에서 tip 으로 내려도 유지한다. 내렸다 올릴 때마다 광고를 새로 요청하지 않기 위해서다.
     */
    fun next(canLoad: Boolean, isDisplayed: Boolean): Boolean = canLoad || isDisplayed
}
