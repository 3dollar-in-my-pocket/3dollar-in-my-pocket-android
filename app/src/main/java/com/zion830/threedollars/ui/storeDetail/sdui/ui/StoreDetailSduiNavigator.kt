package com.zion830.threedollars.ui.storeDetail.sdui.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.domain.home.data.store.ImageModel
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.ui.dialog.AddReviewDialog
import com.zion830.threedollars.ui.dialog.DeleteStoreDialog
import com.zion830.threedollars.ui.dialog.DirectionBottomDialog
import com.zion830.threedollars.ui.dialog.ReportReviewDialog
import com.zion830.threedollars.ui.dialog.ReviewPhotoDialog
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossReviewDetailActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossReviewWriteActivity
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiEffect
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreReviewDetailActivity
import com.zion830.threedollars.utils.FileUtils
import com.zion830.threedollars.utils.showToast
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.threedollar.common.R as CommonR

/**
 * 가게 상세 v2 가 요청한 화면 밖 동작(다이얼로그·다른 화면·공유 등)을 실행한다.
 * 홈 시트와 전체 화면 상세가 같은 규칙으로 동작하도록 한 곳에 모은다.
 *
 * @param fragmentContainerId 정보 수정 화면을 올릴 컨테이너
 * @param launchForResult 결과를 받아야 하는 화면(보스 리뷰 작성·방문 인증)을 띄운다. 돌아오면 호스트가 상세를 갱신한다.
 * @param onClose 상세를 닫는다(시트는 닫기, 전체 화면은 finish).
 */
class StoreDetailSduiNavigator(
    private val activity: FragmentActivity,
    private val fragmentContainerId: Int,
    private val dispatch: (StoreDetailSduiUiIntent) -> Unit,
    private val launchForResult: (Intent) -> Unit,
    private val onClose: () -> Unit,
) {

    fun handleEffect(effect: StoreDetailSduiUiEffect) {
        when (effect) {
            is StoreDetailSduiUiEffect.ShowToast -> showToast(effect.messageRes, effect.message)
            is StoreDetailSduiUiEffect.ShowErrorAlert -> showErrorAlert(effect.message)
            is StoreDetailSduiUiEffect.Close -> close(effect)
            is StoreDetailSduiUiEffect.Navigate -> navigate(effect.destination)
            is StoreDetailSduiUiEffect.ScrollToSection -> Unit
        }
    }

    fun navigate(destination: StoreDetailDestination) {
        when (destination) {
            is StoreDetailDestination.OpenLink -> openLink(destination)
            is StoreDetailDestination.Share -> share(destination.url)
            is StoreDetailDestination.Directions -> DirectionBottomDialog
                .getInstance(destination.latitude, destination.longitude, destination.storeName)
                .show(activity.supportFragmentManager, DirectionBottomDialog::class.java.name)

            is StoreDetailDestination.CopyAddress -> copy(destination.address, CommonR.string.address_copied)
            is StoreDetailDestination.CopyText -> copy(destination.text, CommonR.string.store_detail_text_copied)
            is StoreDetailDestination.MapEnlarge -> activity.startActivity(
                FullScreenMapActivity.getIntent(activity, destination.latitude, destination.longitude, destination.storeName)
            )

            is StoreDetailDestination.WriteReview -> writeReview(destination)
            is StoreDetailDestination.AddImage -> pickImages()
            is StoreDetailDestination.ShowImages -> ReviewPhotoDialog
                .getInstance(destination.imageUrls.map { ImageModel(imageUrl = it, width = 0, height = 0, ratio = 0) }, destination.startIndex)
                .show(activity.supportFragmentManager, ReviewPhotoDialog::class.java.name)

            is StoreDetailDestination.EditStore -> editStore(destination.storeId)
            is StoreDetailDestination.ReportStore -> DeleteStoreDialog.getInstance()
                .apply { setOnSubmitListener { dispatch(StoreDetailSduiUiIntent.OnStoreReportSubmit(it.key)) } }
                .show(activity.supportFragmentManager, DeleteStoreDialog::class.java.name)

            is StoreDetailDestination.ReportReview -> reportReview(destination)
            is StoreDetailDestination.ConfirmDeleteReview -> confirm(
                message = activity.getString(CommonR.string.store_detail_review_delete_confirm),
                positive = activity.getString(CommonR.string.delete),
            ) { dispatch(StoreDetailSduiUiIntent.OnReviewDeleteConfirmed(destination.reviewId)) }

            is StoreDetailDestination.ConfirmUseCoupon -> confirm(
                message = activity.getString(CommonR.string.store_detail_coupon_use_confirm),
                positive = activity.getString(CommonR.string.store_detail_coupon_use),
            ) { dispatch(StoreDetailSduiUiIntent.OnCouponUseConfirmed(destination.issuedKey)) }

            is StoreDetailDestination.Visit -> destination.storeId.toIntOrNull()?.let { storeId ->
                launchForResult(
                    StoreCertificationActivity.getIntent(
                        activity,
                        StoreCertificationArgs(
                            storeId = storeId,
                            storeName = destination.storeName,
                            latitude = destination.latitude,
                            longitude = destination.longitude,
                        )
                    )
                )
            }

            is StoreDetailDestination.ReviewList -> activity.startActivity(
                if (destination.isBossStore) {
                    BossReviewDetailActivity.getIntent(activity, storeId = destination.storeId)
                } else {
                    StoreReviewDetailActivity.getInstance(activity, destination.storeId.toIntOrNull() ?: 0)
                }
            )
        }
    }

    private fun openLink(destination: StoreDetailDestination.OpenLink) {
        val link = destination.link.link?.takeIf { it.isNotBlank() } ?: return
        when {
            destination.link.type == SDLinkType.WEB -> activity.startActivitySafely(Intent(Intent.ACTION_VIEW, link.toUri()))
            link.startsWith(TEL_SCHEME) -> activity.startActivitySafely(Intent(Intent.ACTION_DIAL, link.toUri()))
            else -> DynamicLinkActivity.launch(activity, link)
        }
    }

    private fun share(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        activity.startActivitySafely(Intent.createChooser(intent, null))
    }

    private fun copy(text: String, messageRes: Int) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        clipboard.setPrimaryClip(ClipData.newPlainText(text, text))
        showToast(messageRes)
    }

    private fun writeReview(destination: StoreDetailDestination.WriteReview) {
        if (destination.isBossStore) {
            launchForResult(BossReviewWriteActivity.getIntent(activity, destination.storeId))
            return
        }
        AddReviewDialog.newInstance { contents, rating ->
            dispatch(StoreDetailSduiUiIntent.OnReviewSubmit(contents, rating))
        }.show(activity.supportFragmentManager, AddReviewDialog::class.java.name)
    }

    private fun pickImages() {
        TedImagePicker.with(activity)
            .zoomIndicator(false)
            .startMultiImage { uris ->
                activity.lifecycleScope.launch {
                    val images = withContext(Dispatchers.IO) { FileUtils.toImageParts(uris) }
                    if (images == null) {
                        showToast(CommonR.string.error_file_size)
                    } else {
                        dispatch(StoreDetailSduiUiIntent.OnImagesSelected(images))
                    }
                }
            }
    }

    private fun editStore(storeId: String) {
        val id = storeId.toIntOrNull() ?: return
        val tag = EditStoreFragment::class.java.simpleName
        if (activity.supportFragmentManager.findFragmentByTag(tag) != null) return
        activity.supportFragmentManager.addNewFragment(fragmentContainerId, EditStoreFragment.newInstance(id), tag)
    }

    private fun reportReview(destination: StoreDetailDestination.ReportReview) {
        val storeId = destination.storeId.toIntOrNull() ?: return
        ReportReviewDialog.getInstance(storeId = storeId, reviewId = destination.reviewId).apply {
            setReportReasons(destination.reasons)
            setOnReportClickListener { _, reviewId, request ->
                dispatch(StoreDetailSduiUiIntent.OnReviewReportSubmit(reviewId, request))
            }
        }.show(activity.supportFragmentManager, ReportReviewDialog::class.java.name)
    }

    private fun showToast(messageRes: Int?, message: String?) {
        when {
            messageRes != null -> showToast(messageRes)
            !message.isNullOrBlank() -> showToast(message)
        }
    }

    private fun showErrorAlert(message: String?) {
        if (activity.isFinishing) return
        AlertDialog.Builder(activity)
            .setMessage(message?.takeIf { it.isNotBlank() } ?: activity.getString(CommonR.string.store_detail_error_default))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun close(effect: StoreDetailSduiUiEffect.Close) {
        val message = effect.message ?: effect.messageRes?.let(activity::getString)
        if (effect.asAlert && !message.isNullOrBlank() && !activity.isFinishing) {
            AlertDialog.Builder(activity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ -> onClose() }
                .show()
            return
        }
        message?.let { showToast(it) }
        onClose()
    }

    private fun confirm(message: String, positive: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(positive) { _, _ -> onConfirm() }
            .setNegativeButton(CommonR.string.cancel, null)
            .show()
    }


    private fun Context.startActivitySafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            showErrorAlert(null)
        }
    }

    private companion object {
        const val TEL_SCHEME = "tel:"
    }
}
