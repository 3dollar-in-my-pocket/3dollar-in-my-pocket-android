package com.zion830.threedollars.repository

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.await
import kotlin.coroutines.CoroutineContext

class MyReviewDataSource(
    private val scope: CoroutineScope,
    private val context: CoroutineContext
) : PageKeyedDataSource<Int, ReviewDetail>() {

    private val repository: UserRepository = UserRepository()

    private var totalPage = 0

    class Factory(
        private val scope: CoroutineScope,
        private val context: CoroutineContext
    ) : DataSource.Factory<Int, ReviewDetail>() {

        override fun create(): DataSource<Int, ReviewDetail> = MyReviewDataSource(scope, context)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ReviewDetail>) {
        scope.launch(context) {
            val data = repository.getMyReviews(0)
            callback.onResult((data.body()?.data?.contents) as MutableList<ReviewDetail>, null, 100)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ReviewDetail>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ReviewDetail>) {
        if (params.key > totalPage) {
            return
        }

        scope.launch(context) {
            val data = repository.getMyReviews(params.key)
            callback.onResult((data.body()?.data?.contents) as MutableList<ReviewDetail>, params.key + PAGE_SIZE)
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