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
import com.threedollar.domain.home.request.MenuModelRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
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
                Spacer(modifier = Modifier.height(24.dp))

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
                    iconResId = DesignSystemR.drawable.ic_marker,
                    isModified = state.hasLocationChanges,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.Location))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EditSectionCard(
                    title = stringResource(CommonR.string.edit_store_section_info),
                    description = buildStoreInfoDescription(state),
                    iconResId = DesignSystemR.drawable.ic_info,
                    isModified = state.hasInfoChanges,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreInfo))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EditSectionCard(
                    title = stringResource(CommonR.string.edit_store_section_menu),
                    description = if (state.menuCount > 0) {
                        stringResource(CommonR.string.edit_store_menu_count, state.menuCount)
                    } else {
                        stringResource(CommonR.string.edit_store_no_info)
                    },
                    iconResId = DesignSystemR.drawable.ic_completion_menu,
                    isModified = state.hasMenuChanges,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreMenu))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            MainButton(
                text = stringResource(CommonR.string.edit_store_complete),
                enabled = state.isSubmitEnabled,
                onClick = {
                    onIntent(
                        EditStoreContract.Intent.SubmitEdit(
                            buildSubmitRequest(state)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
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

@Composable
private fun buildStoreInfoDescription(state: EditStoreContract.State): String {
    val parts = mutableListOf<String>()

    if (state.storeName.isNotEmpty()) {
        parts.add(state.storeName)
    }

    state.storeType?.let { type ->
        val typeName = when (type) {
            "ROAD" -> stringResource(CommonR.string.road)
            "STORE" -> stringResource(CommonR.string.store)
            "CONVENIENCE_STORE" -> stringResource(CommonR.string.convenience_store)
            else -> type
        }
        parts.add(typeName)
    }

    if (state.selectedPaymentMethods.isNotEmpty()) {
        val cashLabel = stringResource(CommonR.string.cash)
        val cardLabel = stringResource(CommonR.string.card)
        val bankingLabel = stringResource(CommonR.string.banking)
        val paymentText = state.selectedPaymentMethods.joinToString(", ") { method ->
            when (method.name) {
                "CASH" -> cashLabel
                "CARD" -> cardLabel
                "ACCOUNT_TRANSFER" -> bankingLabel
                else -> method.name
            }
        }
        parts.add(paymentText)
    }

    return if (parts.isEmpty()) {
        stringResource(CommonR.string.edit_store_no_info)
    } else {
        parts.joinToString(" · ")
    }
}

private fun buildSubmitRequest(state: EditStoreContract.State): UserStoreModelRequest {
    val menuRequests = state.selectCategoryList.flatMap { category ->
        val menus = category.menuDetail
        if (menus.isNullOrEmpty()) {
            listOf(
                MenuModelRequest(
                    name = "",
                    price = null,
                    category = category.menuType.categoryId
                )
            )
        } else {
            menus.mapNotNull { menu ->
                if (!menu.name.isNullOrEmpty() || menu.price != null) {
                    MenuModelRequest(
                        name = menu.name ?: "",
                        price = menu.price?.toIntOrNull(),
                        category = category.menuType.categoryId
                    )
                } else {
                    MenuModelRequest(
                        name = "",
                        price = null,
                        category = category.menuType.categoryId
                    )
                }
            }.ifEmpty {
                listOf(
                    MenuModelRequest(
                        name = "",
                        price = null,
                        category = category.menuType.categoryId
                    )
                )
            }
        }
    }

    return UserStoreModelRequest(
        appearanceDays = state.selectedDays.toList(),
        latitude = state.selectedLocation?.latitude ?: 0.0,
        longitude = state.selectedLocation?.longitude ?: 0.0,
        menuRequests = menuRequests,
        paymentMethods = state.selectedPaymentMethods.toList(),
        openingHours = state.openingHours,
        storeName = state.storeName,
        salesType = state.storeType
    )
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
