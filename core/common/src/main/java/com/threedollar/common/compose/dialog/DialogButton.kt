package com.threedollar.common.compose.dialog

/**
 * 다이얼로그 버튼을 정의하는 데이터 클래스
 *
 * @param text 버튼에 표시될 텍스트
 * @param onClick 버튼 클릭 시 실행될 액션
 * @param isPrimary true면 주요 버튼(빨간색 배경), false면 보조 버튼(회색 배경)
 */
data class DialogButton(
    val text: String,
    val onClick: () -> Unit,
    val isPrimary: Boolean = false
)
