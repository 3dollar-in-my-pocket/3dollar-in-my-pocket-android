package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName

data class CommentPollModel(
    @SerializedName("children")
    val children: String = "",
    @SerializedName("current")
    val current: Current = Current()
)