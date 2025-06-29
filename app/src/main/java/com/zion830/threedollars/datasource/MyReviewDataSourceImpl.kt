package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.user.MyReviewResponseData
import com.threedollar.network.data.user.MyReviewResponseV2
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.di.LegacyNetworkModule
import com.zion830.threedollars.network.NewServiceApi

class MyReviewDataSourceImpl(
    private val serverApi: ServerApi,
    private val filterStoreType: String? = null
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
                
                // storeType 필터링 적용
                val filteredData = if (filterStoreType != null) {
                    allData.filter { it.store.storeType == filterStoreType }
                } else {
                    allData
                }
                
                LoadResult.Page(
                    data = filteredData,
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