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
            callback.onResult((data.body()?.data?.contents) as MutableList<StoreInfo>, null, 100)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, StoreInfo>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, StoreInfo>) {
        scope.launch(context) {
            val data = repository.getMyStore(latLng.latitude, latLng.longitude, params.key)
            callback.onResult((data.body()?.data?.contents as MutableList<StoreInfo>), params.key + PAGE_SIZE)
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