package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.response.PopupsResponse
import retrofit2.Response

interface PopupDataSource {
    suspend fun getPopups(position: String) : Response<PopupsResponse>
}