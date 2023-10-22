package com.threedollar.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.threedollar.data.mapper.toMapper
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PopularStore
import com.threedollar.network.api.ServerApi
import javax.inject.Inject

class PopularStoresDataSource(private val cursor: Cursor, private val criteria: String, private val district: String) :
    PagingSource<Cursor, PopularStore>() {

    @Inject
    lateinit var serverApi: ServerApi
    override fun getRefreshKey(state: PagingState<Cursor, PopularStore>): Cursor? = null

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, PopularStore> {
        val cursor = params.key
        if (cursor == null || cursor.nextCursor == "") {
            return LoadResult.Error(Exception())
        }
        return try {
            val response = serverApi.getPopularStores(criteria, district, cursor.nextCursor)

            if (response.isSuccessful) {
                val response = response.body()?.data ?: return LoadResult.Error(NullPointerException())
                val popularStores = response.toMapper()
                LoadResult.Page(
                    data = popularStores.content,
                    null,
                    popularStores.cursor
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}