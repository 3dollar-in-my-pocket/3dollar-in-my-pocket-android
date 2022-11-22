package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import javax.inject.Inject

class MyStoreDataSourceImpl @Inject constructor() :
    PagingSource<Int, StoreInfo>() {

    @Inject
    lateinit var userDataSource: UserDataSource

    override fun getRefreshKey(state: PagingState<Int, StoreInfo>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreInfo> {
        val cursor = params.key
        if (cursor == -1) {
            return LoadResult.Error(Exception())
        }

        return try {
            val response = userDataSource.getMyStore(cursor, LOAD_SIZE)

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