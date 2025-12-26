package com.threedollar.data.my.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.visit_history.MyVisitHistoryV2

class MyVisitHistoryDataSourceImpl(private val serverApi: ServerApi) :
    PagingSource<Int, MyVisitHistoryV2>() {

    override fun getRefreshKey(state: PagingState<Int, MyVisitHistoryV2>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyVisitHistoryV2> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = serverApi.getMyVisitsStore(LOAD_SIZE, cursor?.toString())

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