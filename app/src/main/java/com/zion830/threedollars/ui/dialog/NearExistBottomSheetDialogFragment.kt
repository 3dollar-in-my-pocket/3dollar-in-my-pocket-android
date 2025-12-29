package com.zion830.threedollars.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray80
import base.compose.Pink
import base.compose.Pink100
import base.compose.PretendardFontFamily
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.geometry.LatLng
import com.threedollar.common.utils.Constants.CLICK_ADDRESS_OK
import com.zion830.threedollars.utils.getCurrentLocationName
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

class NearExistBottomSheetDialogFragment : BottomSheetDialogFragment() {

    interface DialogListener {
        fun accept()
    }

    private var listener: DialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val latitude = arguments?.getDouble(LATITUDE) ?: 0.0
                val longitude = arguments?.getDouble(LONGITUDE) ?: 0.0

                @Suppress("UNCHECKED_CAST")
                val nearStores = (arguments?.getSerializable(NEAR_STORES) as? ArrayList<NearStoreInfo>) ?: arrayListOf()
                val address = getCurrentLocationName(LatLng(latitude, longitude)) ?: ""

                NearExistBottomSheetContent(
                    nearStores = nearStores,
                    address = address,
                    onDismiss = { dismiss() },
                    onConfirm = {
                        listener?.accept()
                        dismiss()
                    }
                )
            }
        }
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        private const val NEAR_STORES = "near_stores"

        fun getInstance(
            latitude: Double,
            longitude: Double,
            nearStores: List<NearStoreInfo> = emptyList()
        ) = NearExistBottomSheetDialogFragment().apply {
            arguments = Bundle().apply {
                putDouble(LATITUDE, latitude)
                putDouble(LONGITUDE, longitude)
                putSerializable(NEAR_STORES, ArrayList(nearStores))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NearExistBottomSheetLayout(
    sheetState: ModalBottomSheetState,
    nearStores: List<NearStoreInfo>,
    address: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            NearExistBottomSheetContent(
                nearStores = nearStores,
                address = address,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        },
        content = content
    )
}

@Composable
fun NearExistBottomSheetContent(
    nearStores: List<NearStoreInfo>,
    address: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .padding(horizontal = 20.dp, vertical = 26.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(CommonR.string.dialog_near_exist_title_format, nearStores.size),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 20.dp),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Gray100
            )
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_close_24_gray40),
                    contentDescription = "닫기",
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier
            .border(1.dp, Gray20, RoundedCornerShape(16.dp))
            .padding(12.dp)) {
            Text(
                text = stringResource(CommonR.string.near_store_header),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Gray80
            )

            Spacer(modifier = Modifier.height(8.dp))

            val maxDisplayCount = 3
            val displayStores = nearStores.take(maxDisplayCount)
            val remainingCount = nearStores.size - maxDisplayCount

            displayStores.forEach { store ->
                NearStoreItem(store = store)
                Spacer(modifier = Modifier.height(3.dp))
            }

            if (remainingCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(CommonR.string.more_stores_format, remainingCount),
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Gray50
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Gray10, shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = address.ifEmpty { stringResource(CommonR.string.location_no_address) },
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Gray70,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(CommonR.string.dialog_near_exist_button_finish),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Pink, shape = RoundedCornerShape(12.dp))
                .clickable {
                    val bundle = Bundle().apply {
                        putString("screen", "write_address_popup")
                        putString("address", address)
                    }
                    onConfirm()
                }
                .padding(vertical = 14.dp),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = ColorWhite,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NearStoreItem(
    store: NearStoreInfo
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = store.storeName,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Gray100,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (store.isBossStore) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "사장님 직영",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Pink,
                modifier = Modifier
                    .background(
                        color = Pink100,
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        if (store.categoryName.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "#${store.categoryName}",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Gray70,
                modifier = Modifier
                    .background(
                        color = Gray10,
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NearExistBottomSheetContentPreview() {
    val sampleStores = listOf(
        NearStoreInfo(storeName = "남영열차 붕어빵", isBossStore = true, categoryName = "붕어빵"),
        NearStoreInfo(storeName = "호떡집", isBossStore = false, categoryName = "호떡"),
        NearStoreInfo(storeName = "타코야끼 가게", isBossStore = false, categoryName = "타코야끼"),
        NearStoreInfo(storeName = "와플 가게", isBossStore = true, categoryName = "와플")
    )

    NearExistBottomSheetContent(
        nearStores = sampleStores,
        address = "서울 용산구 한강대로 405",
        onDismiss = {},
        onConfirm = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun NearStoreItemPreview() {
    Column {
        NearStoreItem(
            store = NearStoreInfo(
                storeName = "남영열차 붕어빵",
                isBossStore = true,
                categoryName = "붕어빵"
            )
        )
        NearStoreItem(
            store = NearStoreInfo(
                storeName = "호떡집",
                isBossStore = false,
                categoryName = "호떡"
            )
        )
        NearStoreItem(
            store = NearStoreInfo(
                storeName = "긴 이름의 가게 이름이 들어가면 어떻게 되는지 테스트",
                isBossStore = true,
                categoryName = ""
            )
        )
    }
}
