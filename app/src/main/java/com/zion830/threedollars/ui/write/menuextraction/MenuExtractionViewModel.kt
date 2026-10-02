package com.zion830.threedollars.ui.write.menuextraction

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.threedollar.common.R as CommonR
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.result.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 메뉴판 사진 1장 → AI 메뉴 인식 → 결과 확인·수정까지를 맡는다. 가게 제보와 가게 정보 수정이 함께 쓴다.
 * 결과를 진입 화면에 반영하는 일은 [MenuExtractionEffect.Completed]를 받은 쪽이 한다.
 */
@HiltViewModel
class MenuExtractionViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val photoFileReader: MenuPhotoFileReader,
) : BaseViewModel() {

    private val _state = MutableStateFlow(MenuExtractionState())
    val state: StateFlow<MenuExtractionState> = _state.asStateFlow()

    private val _effect = Channel<MenuExtractionEffect>(Channel.BUFFERED)
    val effect: Flow<MenuExtractionEffect> = _effect.receiveAsFlow()

    private var extractionJob: Job? = null

    fun createCaptureUri(): Uri = photoFileReader.createCaptureUri()

    fun extract(uri: Uri) {
        extractionJob?.cancel()
        _state.value = MenuExtractionState(phase = MenuExtractionState.Phase.LOADING)
        extractionJob = viewModelScope.launch(coroutineExceptionHandler) {
            val part = photoFileReader.readAsPart(uri)
            if (part == null) {
                failWith(message = null, fallbackMessageRes = CommonR.string.error_unknown_title)
                return@launch
            }
            storeRepository.extractStoreMenus(part)
                .onSuccess { menus ->
                    val categories = menus.toSelectCategories()
                    if (categories.isEmpty()) {
                        failWith(message = null, fallbackMessageRes = CommonR.string.menu_extraction_empty_error)
                        return@onSuccess
                    }
                    _state.value = MenuExtractionState(
                        phase = MenuExtractionState.Phase.RESULT,
                        recognizedMenuCount = menus.size,
                        categories = categories,
                        selectedCategoryId = categories.first().menuType.categoryId,
                    )
                }
                .onFailure { throwable ->
                    failWith(
                        message = (throwable as? ApiException)?.message,
                        fallbackMessageRes = CommonR.string.error_unknown_title,
                    )
                }
        }
    }

    /** 로딩 중 닫기·뒤로가기. 요청을 취소하고 진입 화면으로 돌아간다. */
    fun cancel() {
        extractionJob?.cancel()
        extractionJob = null
        _state.value = MenuExtractionState()
    }

    /** 결과 화면 닫기. 인식 결과는 반영하지 않는다. */
    fun closeResult() {
        _state.value = MenuExtractionState()
    }

    fun selectCategory(categoryId: String) {
        _state.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun addMenu(categoryId: String) {
        _state.update { it.copy(categories = it.categories.addEmptyMenu(categoryId)) }
    }

    fun removeMenu(categoryId: String, menuIndex: Int) {
        _state.update { it.copy(categories = it.categories.removeMenu(categoryId, menuIndex)) }
    }

    fun updateMenu(categoryId: String, menuIndex: Int, name: String, price: String, count: Int?) {
        _state.update { it.copy(categories = it.categories.updateMenu(categoryId, menuIndex, name, price, count)) }
    }

    fun register() {
        val categories = _state.value.categories
        when (categories.findMenuInputError()) {
            MenuInputError.MIN_PRICE -> _effect.trySend(MenuExtractionEffect.ShowToast(CommonR.string.menu_input_min_price))
            MenuInputError.MIN_COUNT -> _effect.trySend(MenuExtractionEffect.ShowToast(CommonR.string.menu_input_min_count))
            null -> {
                _state.value = MenuExtractionState()
                _effect.trySend(MenuExtractionEffect.Completed(categories.limitToMaxCategories()))
            }
        }
    }

    private suspend fun failWith(message: String?, fallbackMessageRes: Int) {
        _state.value = MenuExtractionState()
        _effect.send(MenuExtractionEffect.ShowErrorAlert(message = message, fallbackMessageRes = fallbackMessageRes))
    }
}
