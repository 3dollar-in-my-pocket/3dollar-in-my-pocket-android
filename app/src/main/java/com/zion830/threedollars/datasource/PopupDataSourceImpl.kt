package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.AdType
import com.zion830.threedollars.network.NewServiceApi
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PopupDataSourceImpl @Inject constructor(private val newService: NewServiceApi) :
    PopupDataSource {
    override fun getPopups(position: AdType, size: Int?) = flow { emit(newService.getPopups(position = position.name, size)) }
}