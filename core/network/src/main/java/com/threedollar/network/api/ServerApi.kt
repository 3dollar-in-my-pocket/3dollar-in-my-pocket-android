package com.threedollar.network.api

import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import zion830.com.common.base.BaseResponse

interface ServerApi {

    @POST("/v1/poll")
    suspend fun createPoll(@Body pollCreateApiRequest: PollCreateApiRequest): BaseResponse<PollCreateApiResponse>

    @GET("/v1/poll/{pollId}")
    suspend fun getPollId(@Path("pollId") id: String): BaseResponse<GetPollResponse>

    @PUT("/v1/poll/{pollId}/choice")
    suspend fun putPollChoice(@Path("pollId") id: String): BaseResponse<String>

    @DELETE("/v1/poll/{pollId}/choice")
    suspend fun deletePollChoice(@Path("pollId") id: String): BaseResponse<String>

    @POST("/v1/poll/{pollId}/report")
    suspend fun reportPoll(@Path("pollId") id: String): BaseResponse<String>

    @GET("/v1/poll/categories")
    suspend fun getPollCategories(): BaseResponse<PollCategoryApiResponse>

    @GET("/v1/polls")
    suspend fun getPollList(
        @Query("categoryId") categoryId: String,
        @Query("sortType") sortType: String?,
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int = 20,
    ): BaseResponse<GetPollListResponse>

    @GET("/v1/user/poll/policy")
    suspend fun getPollPolicy(): BaseResponse<PollPolicyApiResponse>

    @GET("/v1/user/polls")
    suspend fun getUserPollList(@Query("cursor") cursor: Int?, @Query("size") size: Int = 20): BaseResponse<GetUserPollListResponse>

    @POST("/v1/poll/{pollId}/comment")
    suspend fun createPollComment(
        @Path("pollId") id: String,
        @Body pollCommentApiRequest: PollCommentApiRequest
    ): BaseResponse<PollCommentCreateApiResponse>

    @DELETE("/v1/poll/{pollId}/comment/{commentId}")
    suspend fun deletePollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @PATCH("/v1/poll/{pollId}/comment/{commentId}")
    suspend fun editPollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @POST("/v1/poll/{pollId}/comment/{commentId}/report")
    suspend fun reportPollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @GET("/v1/poll/{pollId}/comments")
    suspend fun getPollCommentList(
        @Path("pollId") id: String,
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int = 20,
    ): BaseResponse<GetPollCommentListResponse>
}