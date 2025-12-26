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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import base.compose.Red
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.Gray10
import com.threedollar.common.R as CommonR
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Pink
import base.compose.PretendardFontFamily
import com.threedollar.domain.home.data.store.SalesType
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.utils.getCurrentLocationName

@Composable
fun RequiredInfoScreen(
    state: AddStoreContract.State,
    onIntent: (AddStoreContract.Intent) -> Unit,
    onLocationChangeClick: () -> Unit = {}
) {
    val displayAddress = remember(state.selectedLocation, state.address) {
        state.address.ifBlank {
            state.selectedLocation?.let { getCurrentLocationName(it) } ?: ""
        }
    }

    LaunchedEffect(state.selectedLocation) {
        state.selectedLocation?.let {
            val computedAddress = getCurrentLocationName(it) ?: ""
            if (computedAddress.isNotBlank() && state.address.isBlank()) {
                onIntent(AddStoreContract.Intent.SetAddress(computedAddress))
            }
        }
    }

    RequiredInfoContent(
        modifier = Modifier,
        storeName = state.storeName,
        onStoreNameChange = { onIntent(AddStoreContract.Intent.SetStoreName(it)) },
        selectedStoreType = state.storeType?.let { SalesType.valueOf(it) },
        onStoreTypeSelect = { onIntent(AddStoreContract.Intent.SetStoreType(it.name)) },
        address = displayAddress,
        onLocationChangeClick = onLocationChangeClick,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RequiredInfoContent(
    modifier: Modifier = Modifier,
    storeName: String,
    onStoreNameChange: (String) -> Unit,
    selectedStoreType: SalesType?,
    onStoreTypeSelect: (SalesType) -> Unit,
    address: String,
    onLocationChangeClick: () -> Unit,
) {
    val storeTypes = listOf(SalesType.ROAD, SalesType.STORE, SalesType.FOOD_TRUCK, SalesType.CONVENIENCE_STORE)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(CommonR.string.add_store_info_input_title),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            fontSize = 24.sp,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(CommonR.string.add_store_name_hint_description),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            color = Gray70
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(CommonR.string.store_name),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = storeName,
            onValueChange = onStoreNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(CommonR.string.add_store_name_placeholder),
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = Gray50
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Gray10,
                focusedBorderColor = Red,
                unfocusedBorderColor = Color.Transparent,
                textColor = Gray100
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(CommonR.string.store_type),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            storeTypes.forEach { type ->
                StoreTypeChip(
                    text = type.title,
                    isSelected = selectedStoreType == type,
                    onClick = { onStoreTypeSelect(type) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(CommonR.string.add_store_location_confirm),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Gray10)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = address,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = Gray100,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(CommonR.string.add_store_change_location),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = Pink,
                modifier = Modifier.clickable { onLocationChangeClick() }
            )
        }
    }
}

@Composable
private fun StoreTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Pink else Gray30
    val textColor = if (isSelected) Pink else Gray40

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StoreTypeChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StoreTypeChip(text = "길거리", isSelected = true, onClick = {})
        StoreTypeChip(text = "매장", isSelected = false, onClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RequiredInfoContentPreview() {
    RequiredInfoContent(
        storeName = "",
        onStoreNameChange = {},
        selectedStoreType = null,
        onStoreTypeSelect = {},
        address = "서울특별시 관악구 독립문로 14길",
        onLocationChangeClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RequiredInfoContentFilledPreview() {
    RequiredInfoContent(
        storeName = "붕어빵역 2번 출구 삼거리 근처 붕어빵 집",
        onStoreNameChange = {},
        selectedStoreType = SalesType.ROAD,
        onStoreTypeSelect = {},
        address = "서울특별시 관악구 독립문로 14길",
        onLocationChangeClick = {}
    )
}
