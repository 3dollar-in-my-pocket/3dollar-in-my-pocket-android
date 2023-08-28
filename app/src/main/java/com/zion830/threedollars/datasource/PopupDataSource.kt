package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.AdType
import com.zion830.threedollars.datasource.model.v2.response.PopupsResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PopupDataSource {
    fun getPopups(position: AdType, size: Int?): Flow<Response<PopupsResponse>>
}