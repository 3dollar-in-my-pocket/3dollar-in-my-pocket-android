package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.di.NetworkModule
import com.zion830.threedollars.network.ServiceApi

class MyReviewDataSourceImpl :
    PagingSource<Int, ReviewDetail>() {

    private val serviceApi: ServiceApi = NetworkModule.serviceApi

    override fun getRefreshKey(state: PagingState<Int, ReviewDetail>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewDetail> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = serviceApi.getMyReviews(cursor, LOAD_SIZE)

            if (response.isSuccessful) {
                LoadResult.Page(
                    data = response.body()?.data?.contents ?: emptyList(),
                    null,
                    response.body()?.data?.nextCursor
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