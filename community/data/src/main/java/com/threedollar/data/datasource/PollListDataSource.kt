package com.threedollar.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.data.mapper.GetPollListResponseMapper.toMapper
import com.threedollar.data.mapper.toMapper
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PollItem
import com.threedollar.network.api.ServerApi
import javax.inject.Inject

class PollListDataSource(private val categoryId: String, private val sortType: String,private val serverApi:ServerApi) :
    PagingSource<String, PollItem>() {
    override fun getRefreshKey(state: PagingState<String, PollItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PollItem> {
        val cursor = params.key
        if (cursor == null || cursor == "") {
            return LoadResult.Error(Exception())
        }
        return try {
            val response = serverApi.getPollList(categoryId, sortType, cursor.toInt())

            if (response.isSuccessful) {
                val responseData = response.body()?.data ?: return LoadResult.Error(NullPointerException())
                val pollList = responseData.toMapper()
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