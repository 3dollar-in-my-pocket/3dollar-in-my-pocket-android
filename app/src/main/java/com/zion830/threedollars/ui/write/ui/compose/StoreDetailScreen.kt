package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import kotlinx.coroutines.launch
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoreDetailScreen(
    viewModel: AddStoreViewModel,
    onNavigateToNext: () -> Unit,
    onSkip: () -> Unit = onNavigateToNext,
    modifier: Modifier = Modifier
) {
    val selectedPaymentMethods by viewModel.selectedPaymentMethods.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val openingHours by viewModel.openingHours.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var currentTimeType by remember { mutableStateOf<TimeType?>(null) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            TimePickerBottomSheet(
                selectedTime = when (currentTimeType) {
                    TimeType.START -> openingHours.startTime
                    TimeType.END -> openingHours.endTime
                    null -> null
                },
                onTimeSelected = { time ->
                    when (currentTimeType) {
                        TimeType.START -> viewModel.setStartTime(time)
                        TimeType.END -> viewModel.setEndTime(time)
                        null -> {}
                    }
                    scope.launch { sheetState.hide() }
                }
            )
        }
    ) {
        StoreDetailContent(
            selectedPaymentMethods = selectedPaymentMethods,
            selectedDays = selectedDays,
            startTime = openingHours.startTime,
            endTime = openingHours.endTime,
            onPaymentMethodToggle = viewModel::togglePaymentMethod,
            onDayToggle = viewModel::toggleDay,
            onStartTimeClick = {
                currentTimeType = TimeType.START
                scope.launch { sheetState.show() }
            },
            onEndTimeClick = {
                currentTimeType = TimeType.END
                scope.launch { sheetState.show() }
            },
            onSkip = onSkip,
            onComplete = onNavigateToNext,
            modifier = modifier
        )
    }
}

@Composable
fun StoreDetailContent(
    selectedPaymentMethods: Set<PaymentType>,
    selectedDays: Set<DayOfTheWeekType>,
    startTime: String?,
    endTime: String?,
    onPaymentMethodToggle: (PaymentType) -> Unit,
    onDayToggle: (DayOfTheWeekType) -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("가게 상세 정보 추가 ")
                    withStyle(style = SpanStyle(color = Gray50, fontSize = 16.sp)) {
                        append("선택")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "가게 세부 정보를 입력하고 더 알찬 정보를 제공해 보세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Gray70,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            PaymentMethodSection(
                selectedPaymentMethods = selectedPaymentMethods,
                onToggle = onPaymentMethodToggle
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppearanceDaySection(
                selectedDays = selectedDays,
                onToggle = onDayToggle
            )

            Spacer(modifier = Modifier.height(32.dp))

            OpeningHoursSection(
                startTime = startTime,
                endTime = endTime,
                onStartTimeClick = onStartTimeClick,
                onEndTimeClick = onEndTimeClick
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            SkipButton(onClick = onSkip)
            Spacer(modifier = Modifier.height(12.dp))
            CompleteButton(onClick = onComplete)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaymentMethodSection(
    selectedPaymentMethods: Set<PaymentType>,
    onToggle: (PaymentType) -> Unit
) {
    SectionHeader(title = "결제방식", showMultiSelectHint = true)

    Spacer(modifier = Modifier.height(12.dp))

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PaymentType.values().forEach { paymentType ->
            PaymentChip(
                paymentType = paymentType,
                isSelected = selectedPaymentMethods.contains(paymentType),
                onClick = { onToggle(paymentType) }
            )
        }
    }
}

@Composable
private fun AppearanceDaySection(
    selectedDays: Set<DayOfTheWeekType>,
    onToggle: (DayOfTheWeekType) -> Unit
) {
    SectionHeader(title = "출몰 요일", showMultiSelectHint = true)

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayOfTheWeekType.values().forEach { day ->
            DayChip(
                day = day,
                isSelected = selectedDays.contains(day),
                onClick = { onToggle(day) }
            )
        }
    }
}

@Composable
private fun OpeningHoursSection(
    startTime: String?,
    endTime: String?,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    SectionHeader(title = "출몰 시간대", showMultiSelectHint = false)

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeSelector(
            selectedTime = startTime,
            placeholder = "시작 시간",
            onClick = onStartTimeClick,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "부터",
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray70,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        TimeSelector(
            selectedTime = endTime,
            placeholder = "종료 시간",
            onClick = onEndTimeClick,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "까지",
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray70,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    showMultiSelectHint: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
                text = "*다중선택 가능",
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Pink
            )
        }
    }
}

@Composable
private fun PaymentChip(
    paymentType: PaymentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) Pink else Gray30,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_check),
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = paymentType.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (isSelected) Pink else Gray100
        )
    }
}

@Composable
private fun DayChip(
    day: DayOfTheWeekType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayLabel = day.dayString.first().toString()

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, if (isSelected) Pink else Gray40, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = if (isSelected) Pink else Gray30
        )
    }
}

@Composable
private fun TimeSelector(
    selectedTime: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Gray10,RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = selectedTime ?: placeholder,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = if (selectedTime != null) Gray100 else Gray50
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimePickerBottomSheet(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    val timeOptions = listOf(
        "오전 6시", "오전 7시", "오전 8시", "오전 9시", "오전 10시", "오전 11시",
        "오후 12시", "오후 1시", "오후 2시", "오후 3시", "오후 4시", "오후 5시",
        "오후 6시", "오후 7시", "오후 8시", "오후 9시", "오후 10시", "오후 11시",
        "오전 12시", "오전 1시", "오전 2시", "오전 3시", "오전 4시", "오전 5시"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "시간 선택",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timeOptions.forEach { time ->
                val isSelected = selectedTime == time
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Pink else Gray30,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(if (isSelected) Pink200 else Color.White)
                        .clickable { onTimeSelected(time) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = if (isSelected) Pink else Gray100
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "건너뛰기",
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray70
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Gray70,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun CompleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Pink)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "제보 완료",
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            fontFamily = PretendardFontFamily,
            color = Color.White
        )
    }
}

private enum class TimeType {
    START, END
}

@Preview(showBackground = true)
@Composable
private fun StoreDetailContentPreview() {
    StoreDetailContent(
        selectedPaymentMethods = setOf(PaymentType.CASH, PaymentType.CARD),
        selectedDays = setOf(
            DayOfTheWeekType.MONDAY,
            DayOfTheWeekType.TUESDAY,
            DayOfTheWeekType.WEDNESDAY,
            DayOfTheWeekType.THURSDAY,
            DayOfTheWeekType.FRIDAY
        ),
        startTime = "오전 11시",
        endTime = "오후 8시",
        onPaymentMethodToggle = {},
        onDayToggle = {},
        onStartTimeClick = {},
        onEndTimeClick = {},
        onSkip = {},
        onComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StoreDetailContentEmptyPreview() {
    StoreDetailContent(
        selectedPaymentMethods = emptySet(),
        selectedDays = emptySet(),
        startTime = null,
        endTime = null,
        onPaymentMethodToggle = {},
        onDayToggle = {},
        onStartTimeClick = {},
        onEndTimeClick = {},
        onSkip = {},
        onComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodSectionPreview() {
    Column(modifier = Modifier
        .background(Color.White)
        .padding(vertical = 16.dp)) {
        PaymentMethodSection(
            selectedPaymentMethods = setOf(PaymentType.CASH, PaymentType.CARD),
            onToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppearanceDaySectionPreview() {
    Column(modifier = Modifier
        .background(Color.White)
        .padding(vertical = 16.dp)) {
        AppearanceDaySection(
            selectedDays = setOf(
                DayOfTheWeekType.MONDAY,
                DayOfTheWeekType.TUESDAY,
                DayOfTheWeekType.FRIDAY
            ),
            onToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OpeningHoursSectionPreview() {
    Column(modifier = Modifier
        .background(Color.White)
        .padding(vertical = 16.dp)) {
        OpeningHoursSection(
            startTime = "오전 11시",
            endTime = "오후 8시",
            onStartTimeClick = {},
            onEndTimeClick = {}
        )
    }
}
