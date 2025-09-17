package com.my.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.user.MyReviewResponseData

class MyReviewDataSourceImpl(
    private val serverApi: ServerApi
) : PagingSource<Int, MyReviewResponseData>() {

    override fun getRefreshKey(state: PagingState<Int, MyReviewResponseData>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyReviewResponseData> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = serverApi.getMyReviews(LOAD_SIZE,cursor.toString())

            if (response.isSuccessful) {
                val allData = response.body()?.data?.contents ?: emptyList()

                LoadResult.Page(
                    data = allData,
                    prevKey = null,
                    nextKey = response.body()?.data?.cursor?.nextCursor?.toIntOrNull()
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