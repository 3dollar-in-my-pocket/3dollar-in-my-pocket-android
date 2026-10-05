package com.threedollar.data.fake

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.StoreMenuExtractionApi
import com.threedollar.network.data.store.StoreMenuExtractionListResponse
import okhttp3.MultipartBody
import retrofit2.Response

class FakeStoreMenuExtractionApi : StoreMenuExtractionApi {
    var response: Response<BaseResponse<StoreMenuExtractionListResponse>> =
        Response.success(BaseResponse(ok = true, data = StoreMenuExtractionListResponse(menus = emptyList())))
    var lastFilePart: MultipartBody.Part? = null

    override suspend fun extractStoreMenus(file: MultipartBody.Part): Response<BaseResponse<StoreMenuExtractionListResponse>> {
        lastFilePart = file
        return response
    }
}
