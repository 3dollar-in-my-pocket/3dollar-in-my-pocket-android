package com.home.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.home.data.asModel
import com.home.domain.data.store.ImageContentModel
import com.threedollar.network.api.ServerApi

class ImagePagingDataSource(private val storeId: Int, private val serverApi: ServerApi) :
    PagingSource<String, ImageContentModel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ImageContentModel> {
        return try {
            val nextPageNumber = params.key
            val response = serverApi.getStoreImages(storeId = storeId, size = 20, cursor = nextPageNumber)
            if (response.isSuccessful) {
                val imagesResponse = response.body()?.data ?: return LoadResult.Error(NullPointerException())

                LoadResult.Page(
                    data = imagesResponse.contents?.map { it.image?.asModel() ?: ImageContentModel() } ?: listOf(),
                    prevKey = null,
                    nextKey = if (!imagesResponse.cursor?.nextCursor.isNullOrEmpty()) {
                        imagesResponse.cursor?.nextCursor
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

    override fun getRefreshKey(state: PagingState<String, ImageContentModel>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}