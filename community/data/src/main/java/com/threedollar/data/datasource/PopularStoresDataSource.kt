package com.threedollar.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.data.mapper.toMapper
import com.threedollar.domain.data.PopularStore
import com.threedollar.network.api.ServerApi

class PopularStoresDataSource(private val criteria: String, private val district: String, private val serverApi: ServerApi) :
    PagingSource<String, PopularStore>() {
    override fun getRefreshKey(state: PagingState<String, PopularStore>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PopularStore> {
        val cursor = params.key
        if (cursor == null || cursor == "") {
            return LoadResult.Error(Exception())
        }
        return try {
            val response = serverApi.getPopularStores(criteria, district, cursor)

            if (response.isSuccessful) {
                val responseData = response.body()?.data ?: return LoadResult.Error(NullPointerException())
                val popularStores = responseData.toMapper()
                LoadResult.Page(
                    data = popularStores.content,
                    null,
                    popularStores.cursor.nextCursor
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}