package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("comment")
    val comment: Comment = Comment(),
    @SerializedName("report")
    val report: Report = Report(),
    @SerializedName("user")
    val user: User = User(),
)