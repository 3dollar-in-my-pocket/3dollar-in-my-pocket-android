package com.threedollar.data.home.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.data.home.asModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.network.api.ServerApi

class ReviewPagingDataSource(private val storeId: Int, private val sort: String, private val serverApi: ServerApi) :
    PagingSource<String, ReviewContentModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ReviewContentModel> {
        return try {
            val nextPageNumber = params.key
            val response = serverApi.getStoreReview(storeId = storeId, size = 20, sort = sort, cursor = nextPageNumber)
            if (response.isSuccessful) {
                val reviews = response.body()?.data ?: return LoadResult.Error(NullPointerException())

                LoadResult.Page(
                    data = reviews.contents?.map { it.asModel() } ?: listOf(),
                    prevKey = null,
                    nextKey = if (!reviews.cursor?.nextCursor.isNullOrEmpty()) {
                        reviews.cursor?.nextCursor
                    } else {
                        null
                    }
                )
            } else {
                LoadResult.Error(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, ReviewContentModel>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}