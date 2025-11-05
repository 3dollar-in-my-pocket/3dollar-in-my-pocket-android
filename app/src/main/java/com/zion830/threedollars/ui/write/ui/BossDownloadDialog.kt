package com.zion830.threedollars.ui.write.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray70
import base.compose.Gray80
import base.compose.Green
import base.compose.Pink
import base.compose.PretendardFontFamily
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.threedollar.common.R

class BossDownloadDialog : BottomSheetDialogFragment() {

    private var onDismissCallback: (() -> Unit)? = null
    private var onConfirmCallback: (() -> Unit)? = null
    private var storeData: StoreData? = null
    private var address: String = ""
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initFirebaseAnalytics()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val benefits = stringArrayResource(R.array.boss_benefit)
                BossDownloadDialogContent(
                    storeData = storeData,
                    address = address,
                    benefits = benefits,
                    onDismiss = {
                        onDismissCallback?.invoke()
                        dismiss()
                    },
                    onConfirm = {
                        onConfirmCallback?.invoke()
                        dismiss()
                    }
                )
            }
        }
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "BossDownloadDialog")
            param(FirebaseAnalytics.Param.SCREEN_NAME, "duplicate_store_check")
        }
    }

    fun setOnDismissListener(callback: () -> Unit): BossDownloadDialog {
        onDismissCallback = callback
        return this
    }

    fun setOnConfirmListener(callback: () -> Unit): BossDownloadDialog {
        onConfirmCallback = callback
        return this
    }

    fun setStoresData(): BossDownloadDialog {
        return this
    }

    fun setAddress(address: String): BossDownloadDialog {
        this.address = address
        return this
    }

    data class StoreData(
        val storeName: String? = null,
        val address: String? = null,
        val distance: Double? = null
    )

    companion object {
        fun newInstance(): BossDownloadDialog {
            return BossDownloadDialog()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BossDownloadDialogContent(
    storeData: BossDownloadDialog.StoreData?,
    address: String,
    benefits: Array<String>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = ColorWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "사장님 앱을 설치하고\n더 편하게 가게를 관리해 보세요",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                color = Gray100
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(com.zion830.threedollars.core.designsystem.R.drawable.ic_close_24_gray40),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = onDismiss)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Green, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("아래의 기능을 ")
                    withStyle(style = SpanStyle(color = Green)) {
                        append("모두 무료로")
                    }
                    append(" 만나실 수 있어요")
                },
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = Gray80
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                benefits.forEach { BossBenefitItem(it) }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Pink, shape = RoundedCornerShape(12.dp))
                .clickable(onClick = { onConfirm })
                .padding(vertical = 14.dp),
            text = "이 장소가 확실해요",
            textAlign = TextAlign.Center,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            color = ColorWhite
        )
    }
}

@Composable
fun BossBenefitItem(benefit: String) {
    Text(
        text = benefit,
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        color = Gray70,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                color = Gray10,
                shape = RoundedCornerShape(40.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

