package com.zion830.threedollars.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zion830.threedollars.datasource.model.v4.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.di.NetworkModule
import com.zion830.threedollars.network.ServiceApi
import com.zion830.threedollars.utils.getErrorMessage

class FavoriteMyFolderDataSourceImpl : PagingSource<String, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>() {

    private val serviceApi: ServiceApi = NetworkModule.serviceApi

    override fun getRefreshKey(state: PagingState<String, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel> {
        val cursor = params.key
        if (cursor == "") {
            return LoadResult.Error(Exception())
        }
        return try {
            val response = serviceApi.getMyFavoriteFolder(cursor, LOAD_SIZE)

            if (response.isSuccessful) {
                val myFavoriteFolderResponse = response.body()?.data ?: return LoadResult.Error(NullPointerException())

                LoadResult.Page(
                    data = myFavoriteFolderResponse.favorites,
                    null,
                    myFavoriteFolderResponse.cursor.nextCursor
                )
            } else {
                LoadResult.Error(Exception(response.errorBody()?.string()?.getErrorMessage()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val LOAD_SIZE = 20
    }
}