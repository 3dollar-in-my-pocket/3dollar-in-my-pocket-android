package com.my.presentation.page.data

import com.my.domain.model.MedalModel

data class MyPageUserInformationData(val name: String, val medal: MedalModel?)

val myPageUserInformationDataPreview = MyPageUserInformationData("몽키스패너", null)