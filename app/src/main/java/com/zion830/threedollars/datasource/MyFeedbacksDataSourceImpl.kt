package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.user.MyFeedbacksData
import com.threedollar.network.data.user.MyReviewResponseData
import com.threedollar.network.data.user.MyReviewResponseV2
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.di.LegacyNetworkModule
import com.zion830.threedollars.network.NewServiceApi

class MyFeedbacksDataSourceImpl(private val serverApi: ServerApi) : PagingSource<Int, MyFeedbacksData>() {

    override fun getRefreshKey(state: PagingState<Int, MyFeedbacksData>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyFeedbacksData> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = serverApi.getMyFeedbacks(LOAD_SIZE, cursor?.toString())

            if (response.isSuccessful) {
                LoadResult.Page(
                    data = response.body()?.data?.contents ?: emptyList(),
                    null,
                    response.body()?.data?.cursor?.nextCursor?.toIntOrNull()
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val LOAD_SIZE = 20
    }
}