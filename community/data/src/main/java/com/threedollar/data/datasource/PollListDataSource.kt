package com.threedollar.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.data.mapper.GetPollListResponseMapper.toMapper
import com.threedollar.data.mapper.toMapper
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PollItem
import com.threedollar.network.api.ServerApi
import javax.inject.Inject

class PollListDataSource(private val cursor: Cursor, private val categoryId: String, private val sortType: String) :
    PagingSource<Cursor, PollItem>() {

    @Inject
    lateinit var serverApi: ServerApi
    override fun getRefreshKey(state: PagingState<Cursor, PollItem>): Cursor? = null

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, PollItem> {
        val cursor = params.key
        if (cursor == null || cursor.nextCursor == "") {
            return LoadResult.Error(Exception())
        }
        return try {
            val response = serverApi.getPollList(categoryId, sortType, cursor.nextCursor.toInt())

            if (response.isSuccessful) {
                val response = response.body()?.data ?: return LoadResult.Error(NullPointerException())
                val pollList = response.toMapper()
                LoadResult.Page(
                    data = pollList.pollItems,
                    null,
                    pollList.cursor
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}