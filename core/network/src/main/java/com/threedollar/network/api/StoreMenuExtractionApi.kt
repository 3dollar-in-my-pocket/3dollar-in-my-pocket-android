package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.store.StoreMenuExtractionListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 메뉴판 사진에서 메뉴를 추출하는 AI API. 응답이 10초 가까이 걸려 일반 API 와 다른 타임아웃의 클라이언트를 쓴다.
 */
interface StoreMenuExtractionApi {
    @Multipart
    @POST("/api/v1/store-menu-extractions")
    suspend fun extractStoreMenus(
        @Part file: MultipartBody.Part,
    ): Response<BaseResponse<StoreMenuExtractionListResponse>>
}
