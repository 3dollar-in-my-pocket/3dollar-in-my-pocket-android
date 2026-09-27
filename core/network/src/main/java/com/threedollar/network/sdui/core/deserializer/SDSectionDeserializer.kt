package com.threedollar.network.sdui.core.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAppearanceDaySectionModel
import com.threedollar.common.sdui.model.section.SDStoreCalloutSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCouponSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCtaSectionModel
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.threedollar.common.sdui.model.section.SDStorePostSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreReviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel
import com.threedollar.common.sdui.model.section.SDStoreVisitSectionModel
import com.threedollar.common.sdui.model.section.SDUnknownSectionModel
import java.lang.reflect.Type

/**
 * `type` 으로 섹션 클래스를 고른다. 모르는 타입이나 형식이 깨진 섹션은 [SDUnknownSectionModel]로 바꿔
 * 한 섹션 때문에 화면 전체가 실패하지 않게 한다.
 */
class SDSectionDeserializer : JsonDeserializer<SDSectionModel> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SDSectionModel {
        val jsonObject = json.takeIf { it.isJsonObject }?.asJsonObject
            ?: throw JsonParseException("SDSection must be an object")
        val rawType = jsonObject["type"]?.takeIf { it.isJsonPrimitive }?.asString
        val modelClass = SDSectionType.from(rawType).modelClass() ?: return SDUnknownSectionModel(rawType)

        return runCatching<SDSectionModel> { context.deserialize(jsonObject, modelClass) }
            .getOrElse { SDUnknownSectionModel(rawType) }
    }

    private fun SDSectionType.modelClass(): Class<out SDSectionModel>? = when (this) {
        SDSectionType.PREVIEW -> SDStorePreviewSectionModel::class.java
        SDSectionType.TAB -> SDStoreTabSectionModel::class.java
        SDSectionType.EDIT -> SDStoreEditSectionModel::class.java
        SDSectionType.COUPON -> SDStoreCouponSectionModel::class.java
        SDSectionType.VISIT -> SDStoreVisitSectionModel::class.java
        SDSectionType.POST -> SDStorePostSectionModel::class.java
        SDSectionType.IMAGE -> SDStoreImageSectionModel::class.java
        SDSectionType.APPEARANCE_DAY -> SDStoreAppearanceDaySectionModel::class.java
        SDSectionType.INFO_V1 -> SDStoreInfoV1SectionModel::class.java
        SDSectionType.INFO_V2 -> SDStoreInfoV2SectionModel::class.java
        SDSectionType.RELATED_STORES -> SDRelatedStoresSectionModel::class.java
        SDSectionType.CTA -> SDStoreCtaSectionModel::class.java
        SDSectionType.REVIEW -> SDStoreReviewSectionModel::class.java
        SDSectionType.CALLOUT -> SDStoreCalloutSectionModel::class.java
        SDSectionType.AD_MOB -> SDStoreAdmobSectionModel::class.java
        SDSectionType.MARGIN -> SDStoreMarginSectionModel::class.java
        SDSectionType.UNKNOWN -> null
    }
}
