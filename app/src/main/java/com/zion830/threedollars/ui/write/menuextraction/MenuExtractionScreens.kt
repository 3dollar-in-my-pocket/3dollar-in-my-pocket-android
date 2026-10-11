package com.zion830.threedollars.ui.write.menuextraction

import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import base.compose.ColorB7B7B7
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray70
import base.compose.Gray80
import base.compose.Pink
import base.compose.PretendardFontFamily
import com.threedollar.common.R as CommonR
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.analytics.sendClick
import com.threedollar.common.compose.dialog.CommonDialog
import com.threedollar.common.compose.dialog.DialogButton
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.ui.write.ui.compose.menuCategoryEditorItems
import com.zion830.threedollars.ui.write.ui.compose.MenuCategoryStickyHeader
import com.zion830.threedollars.ui.write.ui.compose.rememberScrollToAddedMenu

/**
 * AI 메뉴 인식 흐름(사진 선택 모달 → 로딩 → 결과)을 [content] 위에 얹는다.
 * [content]에는 사진 선택 모달을 여는 함수가 전달되고, 이 흐름에서 이미 인식을 썼으면 null 이 전달된다(진입점 숨김).
 * 결과를 [등록하기] 하면 [onCompleted]로 카테고리·메뉴를 넘긴다.
 */
@Composable
fun MenuExtractionHost(
    viewModel: MenuExtractionViewModel,
    screenTitle: String,
    onCompleted: (List<SelectCategoryModel>) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (openPhotoSource: (() -> Unit)?) -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()
    val focusManager = LocalFocusManager.current
    val currentOnCompleted by rememberUpdatedState(onCompleted)
    var isPhotoSourceVisible by remember { mutableStateOf(false) }
    var errorAlert by remember { mutableStateOf<MenuExtractionEffect.ShowErrorAlert?>(null) }
    var captureUri by rememberSaveable { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::extract)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        val uri = captureUri?.let(Uri::parse)
        if (isSaved && uri != null) viewModel.extract(uri)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MenuExtractionEffect.Completed -> currentOnCompleted(effect.categories)
                is MenuExtractionEffect.ShowErrorAlert -> errorAlert = effect
                is MenuExtractionEffect.ShowToast -> Toast.makeText(context, effect.messageRes, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content(
            if (isAvailable) {
                {
                    // 메뉴 입력 중이던 키보드가 모달·결과 화면 위에 남지 않도록 포커스를 푼다.
                    focusManager.clearFocus(force = true)
                    isPhotoSourceVisible = true
                }
            } else {
                null
            },
        )

        when (state.phase) {
            MenuExtractionState.Phase.LOADING -> {
                BackHandler(onBack = viewModel::cancel)
                MenuExtractionLoadingScreen(title = screenTitle, onClose = viewModel::cancel)
            }
            MenuExtractionState.Phase.RESULT -> {
                BackHandler(onBack = viewModel::closeResult)
                MenuExtractionResultScreen(
                    title = screenTitle,
                    state = state,
                    onClose = viewModel::closeResult,
                    onSelectCategory = viewModel::selectCategory,
                    onAddMenu = viewModel::addMenu,
                    onRemoveMenu = viewModel::removeMenu,
                    onUpdateMenu = viewModel::updateMenu,
                    onRegister = viewModel::register,
                )
            }
            MenuExtractionState.Phase.IDLE -> Unit
        }
    }

    if (isPhotoSourceVisible) {
        MenuPhotoSourceDialog(
            onDismiss = { isPhotoSourceVisible = false },
            onPickGallery = {
                LogManager.sendClick(ScreenName.WRITE_DETAIL_MENU_PHOTO_POPUP, LogObjectType.BUTTON, LogObjectId.SELECT_PHOTO)
                isPhotoSourceVisible = false
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onTakePhoto = {
                LogManager.sendClick(ScreenName.WRITE_DETAIL_MENU_PHOTO_POPUP, LogObjectType.BUTTON, LogObjectId.TAKE_PHOTO)
                isPhotoSourceVisible = false
                val uri = viewModel.createCaptureUri()
                captureUri = uri.toString()
                try {
                    cameraLauncher.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, CommonR.string.menu_extraction_camera_unavailable, Toast.LENGTH_SHORT).show()
                }
            },
        )
    }

    errorAlert?.let { alert ->
        CommonDialog(
            title = alert.message?.takeIf { it.isNotBlank() } ?: stringResource(alert.fallbackMessageRes),
            confirmButton = DialogButton(
                text = stringResource(CommonR.string.add_store_confirm),
                onClick = { errorAlert = null },
                isPrimary = true,
            ),
            onDismissRequest = { errorAlert = null },
        )
    }
}

/**
 * 음식 카테고리 선택 화면 타이틀 아래 진입 배너.
 */
@Composable
fun MenuExtractionBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Gray0)
            .border(BorderStroke(1.dp, Gray10), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Gray80),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_menu_extraction_camera),
                contentDescription = null,
                tint = ColorWhite,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(CommonR.string.menu_extraction_banner_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Pink)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(CommonR.string.menu_extraction_banner_button),
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = ColorWhite,
            )
        }
    }
}

@Composable
private fun MenuPhotoSourceDialog(
    onDismiss: () -> Unit,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    LaunchedEffect(Unit) {
        LogManager.sendPageView(ScreenName.WRITE_DETAIL_MENU_PHOTO_POPUP, "MenuPhotoSourceDialog")
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.setDimAmount(PHOTO_SOURCE_DIM_AMOUNT)
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ColorWhite)
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(CommonR.string.menu_extraction_photo_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(CommonR.string.menu_extraction_photo_description),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Gray70,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(DesignSystemR.drawable.img_menu_extraction_example),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.height(20.dp))
            PhotoSourceButton(
                text = stringResource(CommonR.string.menu_extraction_pick_gallery),
                iconRes = DesignSystemR.drawable.ic_menu_extraction_gallery,
                onClick = onPickGallery,
            )
            Spacer(modifier = Modifier.height(12.dp))
            PhotoSourceButton(
                text = stringResource(CommonR.string.menu_extraction_take_photo),
                iconRes = DesignSystemR.drawable.ic_menu_extraction_camera,
                onClick = onTakePhoto,
            )
        }
    }
}

@Composable
private fun PhotoSourceButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, ColorB7B7B7), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Gray70,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray70,
        )
    }
}

@Composable
private fun MenuExtractionLoadingScreen(
    title: String,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray0)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
    ) {
        MenuExtractionTopBar(title = title, onBack = null, onClose = onClose)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieFishLoading(modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(CommonR.string.menu_extraction_loading_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Gray70,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(CommonR.string.menu_extraction_loading_description),
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                fontFamily = PretendardFontFamily,
                color = Gray70,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MenuExtractionResultScreen(
    title: String,
    state: MenuExtractionState,
    onClose: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onAddMenu: (String) -> Unit,
    onRemoveMenu: (String, Int) -> Unit,
    onUpdateMenu: (String, Int, String, String, Int?) -> Unit,
    onRegister: () -> Unit,
) {
    LaunchedEffect(Unit) {
        LogManager.sendPageView(ScreenName.WRITE_DETAIL_MENU_EXTRACTION_RESULT, "MenuExtractionResultScreen")
    }
    val selectedCategory = state.categories.find { it.menuType.categoryId == state.selectedCategoryId }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorWhite)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
    ) {
        MenuExtractionTopBar(title = title, onBack = onClose, onClose = onClose)
        val listState = rememberLazyListState()
        val markMenuAdding = rememberScrollToAddedMenu(listState = listState, selectCategory = selectedCategory)
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            item {
                Text(
                    text = stringResource(CommonR.string.menu_extraction_result_title, state.recognizedMenuCount),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = PretendardFontFamily,
                    color = Gray100,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp),
                )
            }
            stickyHeader {
                MenuCategoryStickyHeader(
                    categories = state.categories,
                    selectedCategory = selectedCategory,
                    onSelect = onSelectCategory,
                    onAddMenu = { categoryId ->
                        markMenuAdding()
                        onAddMenu(categoryId)
                    },
                )
            }
            selectedCategory?.let { category ->
                val categoryId = category.menuType.categoryId
                menuCategoryEditorItems(
                    selectCategory = category,
                    onRemoveMenu = { index -> onRemoveMenu(categoryId, index) },
                    onUpdateMenu = { index, name, price, count -> onUpdateMenu(categoryId, index, name, price, count) },
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Pink)
                .clickable(onClick = onRegister),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(CommonR.string.menu_extraction_register),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = ColorWhite,
            )
        }
    }
}

@Composable
private fun MenuExtractionTopBar(
    title: String,
    onBack: (() -> Unit)?,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorWhite),
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
                    contentDescription = stringResource(CommonR.string.cd_back),
                    tint = Gray100,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = Gray100,
            modifier = Modifier.align(Alignment.Center),
        )
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_close_gray100_24),
                contentDescription = stringResource(CommonR.string.close),
                tint = Color.Unspecified,
            )
        }
    }
}

private const val PHOTO_SOURCE_DIM_AMOUNT = 0.2f
