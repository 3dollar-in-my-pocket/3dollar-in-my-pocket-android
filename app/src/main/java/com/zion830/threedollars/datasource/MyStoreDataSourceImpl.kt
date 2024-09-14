package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.store.MyReportedContent

class MyStoreDataSourceImpl(private val serverApi: ServerApi) :
    PagingSource<Int, MyReportedContent>() {

    override fun getRefreshKey(state: PagingState<Int, MyReportedContent>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MyReportedContent> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = serverApi.getMyStores(0.0, 0.0, LOAD_SIZE, cursor.toString())

            if (response.isSuccessful) {
                LoadResult.Error(Exception())
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