package com.zion830.threedollars.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.await
import kotlin.coroutines.CoroutineContext

class MyStoreDataSource(
    private val scope: CoroutineScope,
    private val context: CoroutineContext,
    private val latLng: LatLng
) : PageKeyedDataSource<Int, StoreInfo>() {

    private val repository: UserRepository = UserRepository()

    private var cursor = -1
    private var totalElements = 0

    class Factory(
        private val scope: CoroutineScope,
        private val context: CoroutineContext,
        private val latLng: LatLng
    ) : DataSource.Factory<Int, StoreInfo>() {

        override fun create(): DataSource<Int, StoreInfo> = MyStoreDataSource(scope, context, latLng)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, StoreInfo>) {
        scope.launch(context) {
            val data = repository.getMyStore(latLng.latitude, latLng.longitude, 0)
            cursor = data?.body()?.data?.nextCursor ?: -1
            callback.onResult((data.body()?.data?.contents) as MutableList<StoreInfo>, null, cursor)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, StoreInfo>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, StoreInfo>) {
        if (params.key == -1) {
            return
        }

        scope.launch(context) {
            val data = repository.getMyStore(latLng.latitude, latLng.longitude, params.key)
            cursor = data?.body()?.data?.nextCursor ?: -1
            callback.onResult((data.body()?.data?.contents as MutableList<StoreInfo>), cursor)
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    companion object {
        private const val PAGE_SIZE = 100

        val pageConfig = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPrefetchDistance(PAGE_SIZE)
            .build()
    }
}