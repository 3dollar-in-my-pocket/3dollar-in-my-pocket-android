package com.threedollar.data.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.common.ext.toStringDefault
import com.threedollar.data.mapper.toPollListMapper
import com.threedollar.domain.data.PollItem
import com.threedollar.network.api.ServerApi

class PollListDataSource(private val categoryId: String, private val sortType: String, private val serverApi: ServerApi) :
    PagingSource<String, PollItem>() {
    override fun getRefreshKey(state: PagingState<String, PollItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PollItem> {
        return try {
            val cursor = params.key
            val response = serverApi.getPollList(categoryId, sortType, cursor)
            Log.e("asdwwww",response.toString())
            if (response.isSuccessful) {
                val responseData = response.body()?.data ?: return LoadResult.Error(NullPointerException())
                val pollList = responseData.toPollListMapper()
                LoadResult.Page(
                    data = pollList.pollItems,
                    null,
                    pollList.cursor.nextCursor
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}