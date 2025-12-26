package com.my.presentation.page.team

import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageTeamViewModel @Inject constructor(): BaseViewModel() {
    override val screenName: ScreenName = ScreenName.TEAM_INFO
}