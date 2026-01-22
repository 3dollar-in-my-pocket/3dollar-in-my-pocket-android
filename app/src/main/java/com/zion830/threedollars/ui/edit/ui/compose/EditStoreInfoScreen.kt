package com.zion830.threedollars.ui.edit.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Pink
import com.threedollar.common.R as CommonR
import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.write.ui.compose.TimePickerBottomSheet
import kotlinx.coroutines.launch

private enum class TimeType {
    START, END
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun EditStoreInfoScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var selectedTimeType by remember { mutableStateOf(TimeType.START) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            TimePickerBottomSheet(
                selectedTime = if (selectedTimeType == TimeType.START) {
                    state.openingHours.startTime
                } else {
                    state.openingHours.endTime
                },
                onTimeSelected = { timeString ->
                    if (selectedTimeType == TimeType.START) {
                        onIntent(EditStoreContract.Intent.UpdateStartTime(timeString))
                    } else {
                        onIntent(EditStoreContract.Intent.UpdateEndTime(timeString))
                    }
                    scope.launch { bottomSheetState.hide() }
                }
            )
        }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorWhite)
            ) {
                EditStoreTopBar(
                    title = stringResource(CommonR.string.edit_store_section_info),
                    showBackButton = true,
                    onBackClick = {
                        onIntent(EditStoreContract.Intent.NavigateBack)
                    },
                    onCloseClick = {
                        if (state.hasAnyChanges) {
                            onIntent(EditStoreContract.Intent.ShowExitConfirmDialog)
                        } else {
                            onIntent(EditStoreContract.Intent.ConfirmExit)
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(title = stringResource(CommonR.string.store_name))
                    Spacer(modifier = Modifier.height(8.dp))
                    EditStoreTextField(
                        value = state.storeName,
                        onValueChange = { onIntent(EditStoreContract.Intent.UpdateStoreName(it)) },
                        placeholder = stringResource(CommonR.string.add_store_name_placeholder)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(title = stringResource(CommonR.string.store_type))
                    Spacer(modifier = Modifier.height(8.dp))
                    StoreTypeSection(
                        selectedType = state.storeType,
                        onTypeSelected = { onIntent(EditStoreContract.Intent.UpdateStoreType(it)) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(
                        title = stringResource(CommonR.string.payment_type),
                        showMultiSelectHint = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodSection(
                        selectedMethods = state.selectedPaymentMethods,
                        onToggle = { onIntent(EditStoreContract.Intent.TogglePaymentMethod(it)) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(
                        title = stringResource(CommonR.string.add_store_appearance_day),
                        showMultiSelectHint = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppearanceDaySection(
                        selectedDays = state.selectedDays,
                        onToggle = { onIntent(EditStoreContract.Intent.ToggleAppearanceDay(it)) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(title = stringResource(CommonR.string.add_store_appearance_time))
                    Spacer(modifier = Modifier.height(8.dp))
                    TimePickerRow(
                        startTime = state.openingHours.startTime,
                        endTime = state.openingHours.endTime,
                        onStartTimeClick = {
                            selectedTimeType = TimeType.START
                            scope.launch { bottomSheetState.show() }
                        },
                        onEndTimeClick = {
                            selectedTimeType = TimeType.END
                            scope.launch { bottomSheetState.show() }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                MainButton(
                    text = stringResource(CommonR.string.edit_store_finish),
                    enabled = true,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateBack)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorWhite.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Pink)
                }
            }

            if (state.showExitConfirmDialog) {
                ExitConfirmDialog(
                    onDismiss = { onIntent(EditStoreContract.Intent.HideExitConfirmDialog) },
                    onConfirm = { onIntent(EditStoreContract.Intent.ConfirmExit) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreTypeSection(
    selectedType: String?,
    onTypeSelected: (String) -> Unit
) {
    val types = listOf(
        "ROAD" to stringResource(CommonR.string.road),
        "STORE" to stringResource(CommonR.string.store),
        "CONVENIENCE_STORE" to stringResource(CommonR.string.convenience_store)
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { (type, label) ->
            SelectableChip(
                label = label,
                isSelected = selectedType == type,
                onClick = { onTypeSelected(type) },
                showCheckIcon = false
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaymentMethodSection(
    selectedMethods: Set<PaymentType>,
    onToggle: (PaymentType) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PaymentType.values().forEach { paymentType ->
            val label = when (paymentType) {
                PaymentType.CASH -> stringResource(CommonR.string.cash)
                PaymentType.CARD -> stringResource(CommonR.string.card)
                PaymentType.ACCOUNT_TRANSFER -> stringResource(CommonR.string.banking)
            }
            SelectableChip(
                label = label,
                isSelected = selectedMethods.contains(paymentType),
                onClick = { onToggle(paymentType) },
                showCheckIcon = true
            )
        }
    }
}

@Composable
private fun AppearanceDaySection(
    selectedDays: Set<DayOfTheWeekType>,
    onToggle: (DayOfTheWeekType) -> Unit
) {
    val days = listOf(
        DayOfTheWeekType.MONDAY,
        DayOfTheWeekType.TUESDAY,
        DayOfTheWeekType.WEDNESDAY,
        DayOfTheWeekType.THURSDAY,
        DayOfTheWeekType.FRIDAY,
        DayOfTheWeekType.SATURDAY,
        DayOfTheWeekType.SUNDAY
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { day ->
            DayCircleButton(
                day = day,
                isSelected = selectedDays.contains(day),
                onClick = { onToggle(day) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditStoreInfoScreenPreview() {
    EditStoreInfoScreen(
        state = EditStoreContract.State(
            storeName = "맛있는 분식집",
            storeType = "ROAD",
            selectedPaymentMethods = setOf(PaymentType.CASH, PaymentType.CARD),
            selectedDays = setOf(DayOfTheWeekType.MONDAY, DayOfTheWeekType.WEDNESDAY, DayOfTheWeekType.FRIDAY)
        ),
        onIntent = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EditStoreInfoScreenEmptyPreview() {
    EditStoreInfoScreen(
        state = EditStoreContract.State(),
        onIntent = {}
    )
}
