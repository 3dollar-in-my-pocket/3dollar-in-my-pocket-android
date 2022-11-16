package com.zion830.threedollars.datasource

import com.zion830.threedollars.network.NewServiceApi
import javax.inject.Inject

class PopupDataSourceImpl @Inject constructor(private val newService: NewServiceApi) :
    PopupDataSource {
    override suspend fun getPopups(position: String) = newService.getPopups(position = position)
}