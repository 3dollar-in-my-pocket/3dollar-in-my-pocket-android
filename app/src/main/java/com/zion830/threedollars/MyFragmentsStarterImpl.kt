package com.zion830.threedollars

import androidx.fragment.app.FragmentActivity
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.MyFragmentStarter
import com.threedollar.common.listener.MyFragments
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.ui.mypage.ui.MyMedalFragment
import com.zion830.threedollars.ui.mypage.ui.MyPageSettingFragment
import com.zion830.threedollars.ui.mypage.ui.MyReviewFragment
import com.zion830.threedollars.ui.mypage.ui.MyStoreFragment
import com.zion830.threedollars.ui.mypage.ui.MyVisitHistoryFragment
import javax.inject.Inject

class MyFragmentsStarterImpl @Inject constructor() : MyFragmentStarter {
    override fun addMyFragments(activity: FragmentActivity, myFragments: MyFragments) {
        when(myFragments){
            MyFragments.MyMedal -> addShowAllMedalFragment(activity)
            MyFragments.MyPageSetting -> addSettingPageFragment(activity)
            MyFragments.MyReview -> addShowAllReviewFragment(activity)
            MyFragments.MyStore -> addShowAllStoreFragment(activity)
            MyFragments.MyVisitHistory -> addShowAllVisitHistoryFragment(activity)
        }
    }
    private fun addSettingPageFragment(activity: FragmentActivity) {
        activity.supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyPageSettingFragment(),
            MyPageSettingFragment::class.java.name,
        )
    }

    private fun addShowAllStoreFragment(activity: FragmentActivity) {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_STORE_BTN_CLICKED)
        activity.supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyStoreFragment(),
            MyStoreFragment::class.java.name,
        )
    }

    private fun addShowAllReviewFragment(activity: FragmentActivity) {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_REVIEW_BTN_CLICKED)
        activity.supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyReviewFragment(),
            MyReviewFragment::class.java.name,
        )
    }

    private fun addShowAllMedalFragment(activity: FragmentActivity) {
        EventTracker.logEvent(Constants.SHOW_ALL_MY_MEDAL_BTN_CLICKED)
        activity.supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyMedalFragment(),
            MyMedalFragment::class.java.name,
        )
    }

    private fun addShowAllVisitHistoryFragment(activity: FragmentActivity) {
        activity.supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyVisitHistoryFragment(),
            MyVisitHistoryFragment::class.java.name,
        )
    }
}