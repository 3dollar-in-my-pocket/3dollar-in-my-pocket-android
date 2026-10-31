package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStorePreviewSectionModel(
    override val sectionId: String?,
    val header: Header?,
    val metadata: Metadata?,
    val contributorActionBar: SDActionBarModel?,
    val actionBars: List<SDActionBarModel>?,
    val images: List<SDImageModel>?,
    val bodies: List<Body>?,
    val additionalInfos: AdditionalInfos?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null,
    val markers: List<Marker>? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.PREVIEW

    data class Header(
        val title: SDTextModel?,
        val badge: SDImageModel?
    )

    data class Metadata(
        val primary: List<SDChipModel>?,
        val secondary: List<SDChipModel>?,
        val separator: SDImageModel?
    )

    data class Body(
        val text: SDTextModel?,
        val style: SDSurfaceStyleModel?
    )

    /** 이 가게의 지도 마커. 서버는 가게 하나의 마커를 내려준다. */
    data class Marker(
        val location: Location?
    )

    data class Location(
        val latitude: Double?,
        val longitude: Double?
    )

    /** `type` 이 `STORE` 일 때만 값이 채워진다. */
    data class AdditionalInfos(
        val type: String?,
        val isSubscriber: Boolean?,
        val storeId: String?,
        val storeType: String?
    )
}
