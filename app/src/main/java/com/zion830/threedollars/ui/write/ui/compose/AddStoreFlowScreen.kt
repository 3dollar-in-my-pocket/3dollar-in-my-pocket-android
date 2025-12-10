package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray70
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.Red
import com.zion830.threedollars.core.designsystem.R
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel

object AddStoreRoute {
    const val REQUIRED_INFO = "required_info"
    const val MENU_CATEGORY = "menu_category"
    const val MENU_DETAIL = "menu_detail"
    const val STORE_DETAIL = "store_detail"
    const val COMPLETION = "completion"
    const val COMPLETION_MENU_DETAIL = "completion_menu_detail"
    const val COMPLETION_STORE_DETAIL = "completion_store_detail"
}

@Composable
fun AddStoreFlowScreen(
    viewModel: AddStoreViewModel,
    onNavigateBack: () -> Unit,
    onCloseClick: () -> Unit,
    onComplete: () -> Unit,
    onLocationChangeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isRequiredInfoValid by viewModel.isRequiredInfoValid.collectAsState()
    val createdStoreId by viewModel.createdStoreId.collectAsState()
    val selectCategoryList by viewModel.selectCategoryList.collectAsState()

    LaunchedEffect(createdStoreId) {
        createdStoreId?.let {
            if (currentRoute == AddStoreRoute.STORE_DETAIL) {
                navController.navigate(AddStoreRoute.COMPLETION)
            }
        }
    }

    val progress = when (currentRoute) {
        AddStoreRoute.REQUIRED_INFO -> 0.25f
        AddStoreRoute.MENU_CATEGORY -> 0.5f
        AddStoreRoute.MENU_DETAIL -> 0.75f
        AddStoreRoute.STORE_DETAIL -> 1f
        else -> 0f
    }

    val buttonText = when (currentRoute) {
        AddStoreRoute.COMPLETION -> "완료"
        AddStoreRoute.STORE_DETAIL -> "작성완료"
        else -> "다음"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (currentRoute != AddStoreRoute.COMPLETION) {
                AddStoreTopBar(
                    title = "가게 제보",
                    progress = progress,
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            onNavigateBack()
                        }
                    },
                    onCloseClick = onCloseClick
                )
            }
        },
        bottomBar = {
            if (currentRoute != AddStoreRoute.COMPLETION) {
                when (currentRoute) {
                    AddStoreRoute.REQUIRED_INFO -> {
                        AddStoreBottomBar(
                            buttonText = buttonText,
                            enabled = isRequiredInfoValid,
                            onClick = {
                                if (isRequiredInfoValid) {
                                    navController.navigate(AddStoreRoute.MENU_CATEGORY)
                                }
                            }
                        )
                    }
                    AddStoreRoute.MENU_CATEGORY -> {
                        AddStoreBottomBar(
                            buttonText = buttonText,
                            enabled = true,
                            onClick = { navController.navigate(AddStoreRoute.MENU_DETAIL) }
                        )
                    }
                    AddStoreRoute.MENU_DETAIL -> {
                        AddStoreBottomBar(
                            buttonText = "다음",
                            enabled = selectCategoryList.isNotEmpty(),
                            showSkipButton = true,
                            onClick = { navController.navigate(AddStoreRoute.STORE_DETAIL) },
                            onSkipClick = { navController.navigate(AddStoreRoute.COMPLETION) }
                        )
                    }
                    AddStoreRoute.STORE_DETAIL -> {
                        AddStoreBottomBar(
                            buttonText = "다음",
                            enabled = true,
                            showSkipButton = true,
                            onClick = { viewModel.submitNewStore() },
                            onSkipClick = { navController.navigate(    AddStoreRoute.COMPLETION) }
                        )
                    }
                    AddStoreRoute.COMPLETION_MENU_DETAIL -> {
                        AddStoreBottomBar(
                            buttonText = "제보 완료",
                            enabled = selectCategoryList.isNotEmpty(),
                            showSkipButton = false,
                            onClick = {
                                viewModel.updateStoreWithDetails {
                                    viewModel.markMenuDetailCompleted()
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                    AddStoreRoute.COMPLETION_STORE_DETAIL -> {
                        AddStoreBottomBar(
                            buttonText = "완료",
                            enabled = true,
                            showSkipButton = false,
                            onClick = {
                                viewModel.updateStoreWithDetails {
                                    viewModel.markStoreDetailCompleted()
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AddStoreRoute.REQUIRED_INFO,
            modifier = Modifier.padding(padding)
        ) {
            composable(AddStoreRoute.REQUIRED_INFO) {
                RequiredInfoScreen(
                    viewModel = viewModel,
                    onLocationChangeClick = onLocationChangeClick
                )
            }
            composable(AddStoreRoute.MENU_CATEGORY) {
                MenuCategoryScreen(
                    viewModel = viewModel
                )
            }
            composable(AddStoreRoute.MENU_DETAIL) {
                MenuDetailScreen(
                    viewModel = viewModel
                )
            }
            composable(AddStoreRoute.STORE_DETAIL) {
                StoreDetailScreen(
                    viewModel = viewModel
                )
            }
            composable(AddStoreRoute.COMPLETION) {
                CompletionScreen(
                    viewModel = viewModel,
                    onComplete = onComplete,
                    onNavigateToMenuDetail = {
                        navController.navigate(AddStoreRoute.COMPLETION_MENU_DETAIL)
                    },
                    onNavigateToStoreDetail = {
                        navController.navigate(AddStoreRoute.COMPLETION_STORE_DETAIL)
                    }
                )
            }
            composable(AddStoreRoute.COMPLETION_MENU_DETAIL) {
                MenuDetailScreen(
                    viewModel = viewModel,
                    isCompletionMode = true
                )
            }
            composable(AddStoreRoute.COMPLETION_STORE_DETAIL) {
                StoreDetailScreen(
                    viewModel = viewModel,
                    isCompletionMode = true
                )
            }
        }
    }
}

@Composable
private fun AddStoreTopBar(
    title: String,
    progress: Float,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp,
                    color = Gray100
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "뒤로 가기",
                        tint = Gray100
                    )
                }
            },
            actions = {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_gray100_24),
                        contentDescription = "닫기"
                    )
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
            elevation = 0.dp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Gray30)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .background(Pink)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddStoreTopBarPreview() {
    AddStoreTopBar(
        title = "가게 제보",
        progress = 0.5f,
        onBackClick = {},
        onCloseClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AddStoreTopBarProgress25Preview() {
    AddStoreTopBar(
        title = "가게 제보",
        progress = 0.25f,
        onBackClick = {},
        onCloseClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AddStoreTopBarProgress100Preview() {
    AddStoreTopBar(
        title = "가게 제보",
        progress = 1f,
        onBackClick = {},
        onCloseClick = {}
    )
}

@Composable
private fun AddStoreBottomBar(
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showSkipButton: Boolean = false,
    onSkipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (showSkipButton) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            SkipButton(onClick = onSkipClick)
            Spacer(modifier = Modifier.height(12.dp))
            MainButton(
                buttonText = buttonText,
                onClick = onClick,
                enabled = enabled
            )
        }
    } else {
        MainButton(
            buttonText = buttonText,
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        )
    }
}

@Composable
private fun MainButton(
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (enabled) Pink else Gray30
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = backgroundColor)
            .clickable(enabled = enabled, onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buttonText,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            color = Color.White
        )
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
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Gray70,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddStoreBottomBarPreview() {
    AddStoreBottomBar(
        buttonText = "다음",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AddStoreBottomBarCompletionPreview() {
    AddStoreBottomBar(
        buttonText = "완료",
        onClick = {}
    )
}
