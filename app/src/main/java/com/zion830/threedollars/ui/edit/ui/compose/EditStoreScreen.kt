package com.zion830.threedollars.ui.edit.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Pink
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract.EditScreen

@Composable
fun EditStoreScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorWhite)
        ) {
            EditStoreTopBar(
                title = stringResource(CommonR.string.edit_store),
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
                Spacer(modifier = Modifier.height(32.dp))

                EditStoreHeader(
                    title = stringResource(CommonR.string.edit_store_select_title),
                    subtitle = stringResource(CommonR.string.edit_store_select_subtitle),
                    changedCount = state.totalChangedCount
                )

                Spacer(modifier = Modifier.height(28.dp))

                EditSectionCard(
                    title = stringResource(CommonR.string.edit_store_section_location),
                    description = state.address.ifEmpty {
                        stringResource(CommonR.string.edit_store_no_info)
                    },
                    iconResId = DesignSystemR.drawable.ic_marker_map,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.Location))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EditSectionCard(
                    title = stringResource(CommonR.string.edit_store_section_info),
                    iconResId = DesignSystemR.drawable.ic_completion_megaphone,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreInfo))
                    }
                ) {
                    StoreInfoContent(state = state)
                }

                Spacer(modifier = Modifier.height(12.dp))

                EditSectionCard(
                    title = stringResource(CommonR.string.edit_store_section_menu),
                    description = if (state.menuCount > 0) {
                        stringResource(CommonR.string.edit_store_menu_count, state.menuCount)
                    } else {
                        stringResource(CommonR.string.edit_store_no_info)
                    },
                    iconResId = DesignSystemR.drawable.ic_completion_menu,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreMenu))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        MainButton(
            text = stringResource(CommonR.string.edit_store_complete),
            enabled = state.isSubmitEnabled,
            onClick = {
                onIntent(EditStoreContract.Intent.SubmitEdit())
            },
            modifier = Modifier.align(alignment = Alignment.BottomCenter).fillMaxWidth()
        )

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

@Composable
private fun StoreInfoContent(state: EditStoreContract.State) {
    val roadLabel = stringResource(CommonR.string.road)
    val storeLabel = stringResource(CommonR.string.store)
    val convenienceStoreLabel = stringResource(CommonR.string.convenience_store)
    val cashLabel = stringResource(CommonR.string.cash)
    val cardLabel = stringResource(CommonR.string.card)
    val bankingLabel = stringResource(CommonR.string.banking)

    val storeTypeText = state.storeType?.let { type ->
        when (type) {
            "ROAD" -> roadLabel
            "STORE" -> storeLabel
            "CONVENIENCE_STORE" -> convenienceStoreLabel
            else -> type
        }
    }

    val paymentMethodsText = if (state.selectedPaymentMethods.isNotEmpty()) {
        state.selectedPaymentMethods.joinToString(", ") { method ->
            when (method.name) {
                "CASH" -> cashLabel
                "CARD" -> cardLabel
                "ACCOUNT_TRANSFER" -> bankingLabel
                else -> method.name
            }
        }
    } else null

    val appearanceDaysText = if (state.selectedDays.isNotEmpty()) {
        state.selectedDays.joinToString(", ") { it.shortDayString }
    } else null

    val hasOpeningHours = !state.openingHours.startTime.isNullOrEmpty() ||
                          !state.openingHours.endTime.isNullOrEmpty()
    val openingHoursText = if (hasOpeningHours) {
        val start = state.openingHours.startTime ?: ""
        val end = state.openingHours.endTime ?: ""
        if (start.isNotEmpty() && end.isNotEmpty()) {
            "$start ~ $end"
        } else if (start.isNotEmpty()) {
            "$start ~"
        } else {
            "~ $end"
        }
    } else null

    Column {
        StoreInfoDetailRow(
            label = stringResource(CommonR.string.edit_store_info_store_name),
            value = state.storeName,
            hasData = state.storeName.isNotEmpty()
        )
        StoreInfoDetailRow(
            label = stringResource(CommonR.string.edit_store_info_store_type),
            value = storeTypeText,
            hasData = state.storeType != null
        )
        StoreInfoDetailRow(
            label = stringResource(CommonR.string.edit_store_info_payment_method),
            value = paymentMethodsText,
            hasData = state.selectedPaymentMethods.isNotEmpty()
        )
        StoreInfoDetailRow(
            label = stringResource(CommonR.string.edit_store_info_appearance_day),
            value = appearanceDaysText,
            hasData = state.selectedDays.isNotEmpty()
        )
        StoreInfoDetailRow(
            label = stringResource(CommonR.string.edit_store_info_opening_hour),
            value = openingHoursText,
            hasData = hasOpeningHours
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditStoreScreenPreview() {
    EditStoreScreen(
        state = EditStoreContract.State(
            address = "서울특별시 강남구 역삼동 123-45",
            storeName = "맛있는 분식집",
            storeType = "ROAD",
            hasLocationChanges = true,
            hasInfoChanges = false,
            hasMenuChanges = false
        ),
        onIntent = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EditStoreScreenEmptyPreview() {
    EditStoreScreen(
        state = EditStoreContract.State(),
        onIntent = {}
    )
}
