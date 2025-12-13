package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import base.compose.PretendardFontFamily
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@Composable
fun StoreDetailScreen(
    state: AddStoreContract.State,
    onIntent: (AddStoreContract.Intent) -> Unit,
    onShowTimePickerSheet: (TimeType) -> Unit,
    modifier: Modifier = Modifier,
    isCompletionMode: Boolean = false
) {
    StoreDetailContent(
        selectedPaymentMethods = state.selectedPaymentMethods,
        selectedDays = state.selectedDays,
        startTime = state.openingHours.startTime,
        endTime = state.openingHours.endTime,
        onPaymentMethodToggle = { onIntent(AddStoreContract.Intent.TogglePaymentMethod(it)) },
        onDayToggle = { onIntent(AddStoreContract.Intent.ToggleDay(it)) },
        onStartTimeClick = { onShowTimePickerSheet(TimeType.START) },
        onEndTimeClick = { onShowTimePickerSheet(TimeType.END) },
        modifier = modifier,
        isCompletionMode = isCompletionMode
    )
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
    modifier: Modifier = Modifier,
    isCompletionMode: Boolean = false
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

@Composable
fun TimePickerBottomSheet(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit
) {
    val periodOptions = listOf("오전", "오후")
    val hourOptions = (1..12).toList()
    val minuteOptions = listOf(0, 10, 20, 30, 40, 50)

    val parsedTime = remember(selectedTime) { parseTimeString(selectedTime) }

    var selectedPeriodIndex by remember { mutableIntStateOf(parsedTime.periodIndex) }
    var selectedHourIndex by remember { mutableIntStateOf(parsedTime.hourIndex) }
    var selectedMinuteIndex by remember { mutableIntStateOf(parsedTime.minuteIndex) }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(
                items = periodOptions,
                selectedIndex = selectedPeriodIndex,
                onItemSelected = { selectedPeriodIndex = it },
                modifier = Modifier.weight(1f)
            )

            WheelPicker(
                items = hourOptions.map { "${it}시" },
                selectedIndex = selectedHourIndex,
                onItemSelected = { selectedHourIndex = it },
                modifier = Modifier.weight(1f)
            )

            WheelPicker(
                items = minuteOptions.map { "${it.toString().padStart(2, '0')}분" },
                selectedIndex = selectedMinuteIndex,
                onItemSelected = { selectedMinuteIndex = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Pink)
                .clickable {
                    val period = periodOptions[selectedPeriodIndex]
                    val hour = hourOptions[selectedHourIndex]
                    val minute = minuteOptions[selectedMinuteIndex]
                    val timeString = "$period ${hour}시 ${minute.toString().padStart(2, '0')}분"
                    onTimeSelected(timeString)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "확인",
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

private data class ParsedTime(
    val periodIndex: Int = 0,
    val hourIndex: Int = 11,
    val minuteIndex: Int = 0
)

private fun parseTimeString(timeString: String?): ParsedTime {
    if (timeString.isNullOrBlank()) return ParsedTime()

    val regex = Regex("(오전|오후)\\s*(\\d+)시(?:\\s*(\\d+)분)?")
    val matchResult = regex.find(timeString) ?: return ParsedTime()

    val period = matchResult.groupValues[1]
    val hour = matchResult.groupValues[2].toIntOrNull() ?: 12
    val minute = matchResult.groupValues.getOrNull(3)?.toIntOrNull() ?: 0

    val periodIndex = if (period == "오전") 0 else 1
    val hourIndex = (hour - 1).coerceIn(0, 11)
    val minuteIndex = (minute / 10).coerceIn(0, 5)

    return ParsedTime(periodIndex, hourIndex, minuteIndex)
}

@Composable
private fun WheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleItemCount = 5
    val itemHeight = 36.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)

    LaunchedEffect(selectedIndex) {
        listState.scrollToItem(selectedIndex)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling) {
                    val centerIndex = listState.firstVisibleItemIndex
                    if (centerIndex in items.indices && centerIndex != selectedIndex) {
                        onItemSelected(centerIndex)
                    }
                }
            }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleItemCount),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Gray10, RoundedCornerShape(8.dp))
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.height(itemHeight * visibleItemCount),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(visibleItemCount / 2) {
                Spacer(modifier = Modifier.height(itemHeight))
            }
            itemsIndexed(items) { index, item ->
                val isSelected by remember {
                    derivedStateOf { listState.firstVisibleItemIndex == index }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = if (isSelected) 18.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = if (isSelected) Gray100 else Gray50
                    )
                }
            }
            items(visibleItemCount / 2) {
                Spacer(modifier = Modifier.height(itemHeight))
            }
        }
    }
}

enum class TimeType {
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
        onEndTimeClick = {}
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
        onEndTimeClick = {}
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
