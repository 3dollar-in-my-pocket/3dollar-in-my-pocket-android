package com.zion830.threedollars.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent

class MyVisitHistoryDataSource : PagingSource<Int, VisitHistoryContent>() {

    private val userRepository = UserRepository()

    override fun getRefreshKey(state: PagingState<Int, VisitHistoryContent>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VisitHistoryContent> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = userRepository.getMyVisitHistory(cursor, LOAD_SIZE)

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