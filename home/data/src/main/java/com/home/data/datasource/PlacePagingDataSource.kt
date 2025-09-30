package com.home.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.home.data.asModel
import com.home.domain.data.place.PlaceModel
import com.threedollar.network.api.ServerApi
import com.threedollar.network.request.PlaceType

class PlacePagingDataSource(private val placeType: PlaceType, private val serverApi: ServerApi) :
    PagingSource<String, PlaceModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PlaceModel> {
        return try {
            val nextPageNumber = params.key
            val response = serverApi.getPlace(placeType = placeType.name, size = 20, cursor = nextPageNumber)
            if (response.isSuccessful) {
                val placeResponse = response.body()?.data ?: return LoadResult.Error(NullPointerException())

                LoadResult.Page(
                    data = placeResponse.contents.map { it.asModel() },
                    prevKey = null,
                    nextKey = if (placeResponse.cursor.hasMore && !placeResponse.cursor.nextCursor.isNullOrEmpty()) {
                        placeResponse.cursor.nextCursor
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

    override fun getRefreshKey(state: PagingState<String, PlaceModel>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}