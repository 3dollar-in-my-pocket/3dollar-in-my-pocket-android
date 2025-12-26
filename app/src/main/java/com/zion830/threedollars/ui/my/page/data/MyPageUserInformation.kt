package com.zion830.threedollars.ui.my.page.data

import com.threedollar.domain.my.model.MedalModel

data class MyPageUserInformationData(val name: String, val medal: MedalModel?)

val myPageUserInformationDataPreview = MyPageUserInformationData("몽키스패너", null)