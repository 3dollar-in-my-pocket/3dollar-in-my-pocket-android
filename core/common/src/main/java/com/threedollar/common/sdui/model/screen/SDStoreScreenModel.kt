package com.threedollar.common.sdui.model.screen

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel

/**
 * `GET /api/v2/screen/store/{storeId}` 응답. 섹션은 서버 배열 순서 그대로 렌더한다.
 */
data class SDStoreScreenModel(
    val sections: List<SDSectionModel>?,
    val viewLog: SDLogModel?
)
