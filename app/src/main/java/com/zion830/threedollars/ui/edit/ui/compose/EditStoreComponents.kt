package com.zion830.threedollars.ui.edit.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Gray60
import base.compose.Gray70
import base.compose.Green
import base.compose.Green100
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.Red
import base.compose.dpToSp
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@Composable
fun EditStoreTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
    ) {
        if (showBackButton && onBackClick != null) {
            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_arrow_left),
                contentDescription = null,
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .size(24.dp)
                    .clickable { onBackClick() },
                tint = Gray100
            )
        }
        Text(
            text = title,
            fontSize = dpToSp(16),
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray100,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Icon(
            painter = painterResource(id = DesignSystemR.drawable.ic_close_gray100_24),
            contentDescription = stringResource(id = CommonR.string.close),
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .size(24.dp)
                .clickable { onCloseClick() },
            tint = Gray100
        )
    }
}

/**
 * 헤더 영역 (제목 + 부제 + 수정 배지)
 */
@Composable
fun EditStoreHeader(
    title: String,
    subtitle: String,
    changedCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (changedCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Green100)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = CommonR.string.edit_store_changed_count, changedCount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = PretendardFontFamily,
                    color = Green
                )
            }
        }else{
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Gray70
            )
        }
    }
}

/**
 * 섹션 카드 (가게 위치/정보/메뉴)
 * description을 사용하면 기존처럼 단일 라인 표시
 * content를 사용하면 커스텀 컨텐츠 표시 (가게 정보 상세용)
 */
@Composable
fun EditSectionCard(
    title: String,
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(ColorWhite)
            .clickable { onClick() }
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Gray10),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (content != null) {
                content()
            } else if (description != null) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = PretendardFontFamily,
                    color = Gray50,
                    maxLines = 2
                )
            }
        }
    }
}


/**
 * 가게 정보 상세 행 (체크마크 + 라벨 + 값)
 * hasData가 true면 핑크 체크마크와 값 표시
 * hasData가 false면 체크 없이 "제보된 정보가 없어요" 표시
 */
@Composable
fun StoreInfoDetailRow(
    label: String,
    value: String?,
    hasData: Boolean,
    noDataText: String = stringResource(CommonR.string.edit_store_no_info),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_check_pink),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (hasData) Pink else Gray40
            )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (hasData) Pink else Gray40,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (hasData && !value.isNullOrEmpty()) value else noDataText,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (hasData) Gray60 else Gray50,
            maxLines = 1
        )
    }
}

/**
 * 선택 가능한 칩 (단일/다중 선택)
 */
@Composable
fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCheckIcon: Boolean = false
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) Pink else Gray30,
                shape = RoundedCornerShape(8.dp)
            )
            .background(ColorWhite)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showCheckIcon && isSelected) {
            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Pink
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (isSelected) Pink else Gray100
        )
    }
}

/**
 * 요일 선택 원형 버튼 (월~일)
 */
@Composable
fun DayCircleButton(
    day: DayOfTheWeekType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayLabel = day.shortDayString

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) Pink else Gray10)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (isSelected) ColorWhite else Gray100
        )
    }
}

/**
 * 시간 선택 행 (시작시간 ~ 종료시간)
 */
@Composable
fun TimePickerRow(
    startTime: String?,
    endTime: String?,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeBox(
            time = startTime,
            placeholder = "시작 시간",
            onClick = onStartTimeClick,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "부터",
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray70
        )
        TimeBox(
            time = endTime,
            placeholder = "종료 시간",
            onClick = onEndTimeClick,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "까지",
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray70
        )
    }
}

@Composable
private fun TimeBox(
    time: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Gray10)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time ?: placeholder,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (time != null) Gray100 else Gray50,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 가게 이름 입력 필드
 */
@Composable
fun EditStoreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Gray10)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        singleLine = singleLine,
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray100
        ),
        cursorBrush = SolidColor(Pink),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray50
                    )
                }
                innerTextField()
            }
        }
    )
}

/**
 * 섹션 제목 + 다중선택 힌트
 */
@Composable
fun SectionHeader(
    title: String,
    showMultiSelectHint: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )
        if (showMultiSelectHint) {
            Text(
                text = stringResource(id = CommonR.string.enable_multi_select),
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Gray50
            )
        }
    }
}

/**
 * 닫기 확인 다이얼로그
 */
@Composable
fun ExitConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ColorWhite)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(id = CommonR.string.exit_confirm_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = CommonR.string.edit_store_exit_message),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Gray70
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Gray30, RoundedCornerShape(8.dp))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = CommonR.string.exit_confirm_dismiss),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = PretendardFontFamily,
                        color = Gray100
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Red)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = CommonR.string.exit_confirm_exit),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = PretendardFontFamily,
                        color = ColorWhite
                    )
                }
            }
        }
    }
}

/**
 * 하단 메인 버튼 (수정 완료)
 */
@Composable
fun MainButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(if (enabled) Pink else Gray30)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            fontFamily = PretendardFontFamily,
            color = ColorWhite
        )
    }
}

// ===================== Previews =====================

@Preview(showBackground = true)
@Composable
private fun EditStoreTopBarPreview() {
    EditStoreTopBar(
        title = "가게 정보 수정",
        onCloseClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EditStoreHeaderPreview() {
    EditStoreHeader(
        title = "제보할 정보를\n선택해 주세요",
        subtitle = "작은 제보로 가게 정보 완성에 힘을 보태요",
        changedCount = 2
    )
}

@Preview(showBackground = true)
@Composable
private fun EditSectionCardPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditSectionCard(
            title = "가게 위치",
            description = "서울시 강남구 테헤란로 123",
            iconResId = DesignSystemR.drawable.ic_marker,
            onClick = {}
        )
        EditSectionCard(
            title = "가게 정보",
            description = "붕어빵 가게 · 현금, 카드 · 월, 화, 수",
            iconResId = DesignSystemR.drawable.ic_info,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectableChip(
            label = "현금",
            isSelected = true,
            showCheckIcon = true,
            onClick = {}
        )
        SelectableChip(
            label = "카드",
            isSelected = false,
            showCheckIcon = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DayCircleButtonPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DayCircleButton(
            day = DayOfTheWeekType.MONDAY,
            isSelected = true,
            onClick = {}
        )
        DayCircleButton(
            day = DayOfTheWeekType.TUESDAY,
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerRowPreview() {
    TimePickerRow(
        startTime = "09:00",
        endTime = null,
        onStartTimeClick = {},
        onEndTimeClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EditStoreTextFieldPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EditStoreTextField(
            value = "",
            onValueChange = {},
            placeholder = "가게 이름을 입력해 주세요"
        )
        EditStoreTextField(
            value = "붕어빵 가게",
            onValueChange = {},
            placeholder = "가게 이름을 입력해 주세요"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "가게 이름")
        SectionHeader(title = "결제 방식", showMultiSelectHint = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun MainButtonPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MainButton(
            text = "수정 완료",
            enabled = true,
            onClick = {}
        )
        MainButton(
            text = "수정 완료",
            enabled = false,
            onClick = {}
        )
    }
}
