package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel

internal fun StoreDetailScreenModel?.resolveStoreDetailLocation(
    action: SDCustomActionModel? = null,
    storeId: Long? = null,
    markerStoreId: Long? = null,
    markerLocation: SDLocationModel? = null,
): SDLocationModel? {
    val latitude = action?.extraParams?.get("LATITUDE")?.anyValue?.toString()?.toDoubleOrNull()
    val longitude = action?.extraParams?.get("LONGITUDE")?.anyValue?.toString()?.toDoubleOrNull()
    if (latitude != null && longitude != null) {
        SDLocationModel(latitude, longitude).takeIf { it.isValidLocation() }?.let { return it }
    }
    val sections = this?.sections.orEmpty()
    sections.filterIsInstance<StoreDetailSectionModel.Edit>()
        .firstNotNullOfOrNull { it.map?.location?.takeIf(SDLocationModel::isValidLocation) }
        ?.let { return it }
    sections.filterIsInstance<StoreDetailSectionModel.Map>()
        .firstOrNull { it.location.isValidLocation() }
        ?.let { return it.location }
    return markerLocation?.takeIf {
        storeId != null && storeId == markerStoreId && it.isValidLocation()
    }
}

private fun SDLocationModel.isValidLocation(): Boolean =
    latitude.isFinite() && longitude.isFinite() && latitude in -90.0..90.0 && longitude in -180.0..180.0
