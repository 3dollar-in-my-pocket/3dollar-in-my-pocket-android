package com.threedollar.common.compose.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import base.compose.ColorWhite
import base.compose.Gray100
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Gray70
import base.compose.PretendardFontFamily
import base.compose.Red
import base.compose.dpToSp

/**
 * 공통 다이얼로그 컴포넌트
 *
 * @param title 다이얼로그 제목 (필수)
 * @param description 설명 텍스트 (선택, null이면 표시 안 함)
 * @param confirmButton 확인 버튼 (선택, null이면 표시 안 함)
 * @param dismissButton 취소 버튼 (선택, null이면 표시 안 함)
 * @param content 커스텀 컨텐츠 영역 (선택, null이면 description 사용)
 * @param onDismissRequest 다이얼로그 외부 클릭 또는 뒤로가기 시 호출
 * @param properties 다이얼로그 속성 설정
 */
@Composable
fun CommonDialog(
    title: String,
    description: String? = null,
    confirmButton: DialogButton? = null,
    dismissButton: DialogButton? = null,
    content: (@Composable () -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 타이틀
            Text(
                text = title,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                modifier = Modifier.fillMaxWidth(),
                color = Gray100,
                fontSize = dpToSp(20)
            )

            // 디스크립션 또는 커스텀 컨텐츠
            if (content != null) {
                // 커스텀 컨텐츠가 있으면 그것을 표시
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    content()
                }
            } else if (description != null) {
                // 디스크립션이 있으면 표시
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.W400,
                    color = Gray70,
                    fontSize = dpToSp(14),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }

            // 버튼 영역
            DialogButtons(
                confirmButton = confirmButton,
                dismissButton = dismissButton
            )
        }
    }
}

/**
 * 다이얼로그 버튼 영역
 */
@Composable
private fun DialogButtons(
    confirmButton: DialogButton?,
    dismissButton: DialogButton?
) {
    when {
        // 두 버튼 모두 있는 경우: 가로로 배치
        confirmButton != null && dismissButton != null -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 취소 버튼 (왼쪽)
                DialogActionButton(
                    button = dismissButton,
                    modifier = Modifier.weight(1f)
                )

                // 확인 버튼 (오른쪽)
                DialogActionButton(
                    button = confirmButton,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        // 확인 버튼만 있는 경우
        confirmButton != null -> {
            DialogActionButton(
                button = confirmButton,
                modifier = Modifier.fillMaxWidth()
            )
        }
        // 취소 버튼만 있는 경우
        dismissButton != null -> {
            DialogActionButton(
                button = dismissButton,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 다이얼로그 액션 버튼
 */
@Composable
private fun DialogActionButton(
    button: DialogButton,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = button.onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (button.isPrimary) {
                Red
            } else {
                ColorWhite
            }
        ),
        border = if (button.isPrimary) null else BorderStroke(1.dp, Gray40),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = button.text,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = dpToSp(14),
            color = if (button.isPrimary) Color.White else Gray50,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 2버튼 다이얼로그 프리뷰
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CommonDialogTwoButtonsPreview() {
    CommonDialog(
        title = "다음에 할까요?",
        description = "지금까지 입력한 정보가 저장되지 않아요.",
        dismissButton = DialogButton(
            text = "닫기",
            onClick = {}
        ),
        confirmButton = DialogButton(
            text = "나가기",
            onClick = {},
            isPrimary = true
        )
    )
}

/**
 * 1버튼 다이얼로그 프리뷰
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CommonDialogOneButtonPreview() {
    CommonDialog(
        title = "저장되었습니다",
        description = "정상적으로 저장되었어요.",
        confirmButton = DialogButton(
            text = "확인",
            onClick = {},
            isPrimary = true
        )
    )
}

/**
 * 제목만 있는 다이얼로그 프리뷰
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CommonDialogTitleOnlyPreview() {
    CommonDialog(
        title = "알림",
        confirmButton = DialogButton(
            text = "확인",
            onClick = {},
            isPrimary = true
        )
    )
}

/**
 * 긴 텍스트 다이얼로그 프리뷰
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CommonDialogLongTextPreview() {
    CommonDialog(
        title = "정말 삭제하시겠습니까?",
        description = "삭제된 데이터는 복구할 수 없습니다.\n정말로 삭제하시겠습니까?",
        dismissButton = DialogButton(
            text = "취소",
            onClick = {}
        ),
        confirmButton = DialogButton(
            text = "삭제",
            onClick = {},
            isPrimary = true
        )
    )
}
