package com.threedollar.data.fake

import com.google.gson.Gson
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServiceCreatorApi
import com.threedollar.network.data.creator.ContentListCreatorGroupResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeServiceCreatorApi : ServiceCreatorApi {

    var response: Response<BaseResponse<ContentListCreatorGroupResponse>> =
        Response.success(BaseResponse(ok = true, data = ContentListCreatorGroupResponse()))

    fun givenFixture(path: String) {
        val json = requireNotNull(javaClass.classLoader?.getResource(path)).readText()
        response = Response.success(
            BaseResponse(ok = true, data = Gson().fromJson(json, ContentListCreatorGroupResponse::class.java))
        )
    }

    fun givenError(code: Int, body: String) {
        response = Response.error(code, body.toResponseBody())
    }

    override suspend fun listCreators(): Response<BaseResponse<ContentListCreatorGroupResponse>> = response
}
