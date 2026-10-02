package com.zion830.threedollars.ui.write.menuextraction

import androidx.annotation.StringRes
import com.threedollar.domain.home.data.store.SelectCategoryModel

data class MenuExtractionState(
    val phase: Phase = Phase.IDLE,
    val recognizedMenuCount: Int = 0,
    val categories: List<SelectCategoryModel> = emptyList(),
    val selectedCategoryId: String? = null,
) {
    enum class Phase { IDLE, LOADING, RESULT }
}

sealed interface MenuExtractionEffect {
    /** 서버 오류·인식 0건. [message]가 비어 있으면 [fallbackMessageRes]를 쓴다. 확인하면 진입했던 화면에 그대로 남는다. */
    data class ShowErrorAlert(val message: String?, @StringRes val fallbackMessageRes: Int) : MenuExtractionEffect
    data class ShowToast(@StringRes val messageRes: Int) : MenuExtractionEffect

    /** [등록하기]를 눌렀을 때 진입 화면에 덮어쓸 카테고리·메뉴. */
    data class Completed(val categories: List<SelectCategoryModel>) : MenuExtractionEffect
}
