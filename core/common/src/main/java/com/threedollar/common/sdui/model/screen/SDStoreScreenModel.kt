package com.threedollar.common.sdui.model.screen

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel

/**
 * `GET /api/v2/screen/store/{storeId}` 응답. 섹션은 서버 배열 순서 그대로 렌더한다.
 */
data class SDStoreScreenModel(
    val sections: List<SDSectionModel>?,
    val viewLog: SDLogModel?
) {
    /** PREVIEW 섹션의 첫 마커 좌표. 마커나 좌표가 없으면 null. */
    val previewMarkerLocation: SDStorePreviewSectionModel.Location?
        get() = sections.orEmpty()
            .filterIsInstance<SDStorePreviewSectionModel>()
            .firstOrNull()
            ?.markers
            ?.firstOrNull()
            ?.location
            ?.takeIf { it.latitude != null && it.longitude != null }
}
