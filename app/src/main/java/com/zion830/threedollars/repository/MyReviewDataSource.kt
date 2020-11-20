package com.zion830.threedollars.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.zion830.threedollars.repository.model.response.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.await

class MyReviewDataSource(private val scope: CoroutineScope) : PageKeyedDataSource<Int, Review>() {

    private val repository: UserRepository = UserRepository()

    private var totalPage = 0

    class Factory(
        private val scope: CoroutineScope
    ) : DataSource.Factory<Int, Review>() {

        override fun create(): DataSource<Int, Review> = MyReviewDataSource(scope)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Review>) {
        scope.launch(Dispatchers.IO) {
            val data = repository.getMyReviews().await()
            totalPage = data.totalPages
            callback.onResult(data.review ?: listOf(), null, 2)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) {
        if (params.key > totalPage) {
            return
        }

        scope.launch(Dispatchers.IO) {
            val data = repository.getMyReviews(page = params.key).await()
            callback.onResult(data.review ?: listOf(), params.key + 1)
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    companion object {
        private const val PAGE_SIZE = 3

        val pageConfig = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPrefetchDistance(PAGE_SIZE)
            .build()
    }
}