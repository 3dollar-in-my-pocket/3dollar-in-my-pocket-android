package com.my.presentation.page.data

import com.threedollar.network.data.user.Medal

data class MyPageUserInformationData(val name: String, val medal: Medal)

val myPageUserInformationDataPreview = MyPageUserInformationData("몽키스패너",Medal())