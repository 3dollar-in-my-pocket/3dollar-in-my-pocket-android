package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.data.my.datasource.MyVisitHistoryDataSourceImpl
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.visit_history.MyVisitHistoryV2
import com.zion830.threedollars.ui.mypage.model.MyVisitDeleteResult
import com.zion830.threedollars.ui.mypage.model.createDeleteVisitClickEvent
import com.zion830.threedollars.ui.mypage.model.createVisitHistoryEditClickEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVisitHistoryViewModel @Inject constructor(
    private val serverApi: ServerApi,
    private val homeRepository: HomeRepository,
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.VISITED_LIST

    val myHistoryPager =
        Pager(PagingConfig(MyVisitHistoryDataSourceImpl.LOAD_SIZE)) {
            MyVisitHistoryDataSourceImpl(serverApi)
        }.flow

    private val _isEditMode = MutableStateFlow(false)

    /** 편집 모드면 모든 항목에 삭제 버튼을 보이고, 항목을 눌러도 가게 상세로 가지 않는다. */
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _deleteResult = MutableSharedFlow<MyVisitDeleteResult>(extraBufferCapacity = 1)
    internal val deleteResult: SharedFlow<MyVisitDeleteResult> = _deleteResult.asSharedFlow()

    fun toggleEditMode() {
        if (!_isEditMode.value) LogManager.sendEvent(createVisitHistoryEditClickEvent())
        _isEditMode.value = !_isEditMode.value
    }

    fun exitEditMode() {
        _isEditMode.value = false
    }

    /** 확인 팝업에서 삭제를 눌렀을 때만 부른다. 삭제 가능 기간은 서버가 판단한다. */
    fun deleteVisit(item: MyVisitHistoryV2) {
        val visitId = item.visit.visitId?.takeIf { it.isNotBlank() } ?: return
        LogManager.sendEvent(
            createDeleteVisitClickEvent(
                storeId = item.store.storeId.orEmpty(),
                storeType = item.store.storeType.orEmpty(),
                visitId = visitId,
            )
        )
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteStoreVisit(visitId)
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _deleteResult.emit(MyVisitDeleteResult.fromError())
                }
                .collect { response -> _deleteResult.emit(MyVisitDeleteResult.from(response)) }
        }
    }
}
