package com.threedollar.common.listener

import androidx.fragment.app.FragmentActivity

/**
 * 한 동안 모듈이 전체 분리될때까지 APP모듈에 있는 특정 마이프래그먼트에 접근하기 위한 인터페이스입니다.
 */
interface MyFragmentStarter {
    fun addMyFragments(activity: FragmentActivity, myFragments: MyFragments)
}

sealed class MyFragments {
    object MyPageSetting : MyFragments()
    object MyStore : MyFragments()
    object MyReview : MyFragments()
    object MyMedal : MyFragments()
    object MyVisitHistory : MyFragments()
}
