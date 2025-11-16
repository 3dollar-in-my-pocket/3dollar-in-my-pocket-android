package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel

object AddStoreRoute {
    const val REQUIRED_INFO = "required_info"
    const val MENU_CATEGORY = "menu_category"
    const val MENU_DETAIL = "menu_detail"
    const val STORE_DETAIL = ""
    const val COMPLETION = "completion"
}

@Composable
fun AddStoreFlowScreen(store_detail
    viewModel: AddStoreViewModel,
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddStoreTopBar(
                title = "가게 제보",
                onBackClick = {
                    if (!navController.popBackStack()) {
                        onNavigateBack()
                    }
                }
            )
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
                    onNavigateToNext = { navController.navigate(AddStoreRoute.MENU_CATEGORY) }
                )
            }
            composable(AddStoreRoute.MENU_CATEGORY) {
                MenuCategoryScreen(
                    viewModel = viewModel,
                    onNavigateToNext = { navController.navigate(AddStoreRoute.MENU_DETAIL) }
                )
            }
            composable(AddStoreRoute.MENU_DETAIL) {
                MenuDetailScreen(
                    viewModel = viewModel,
                    onNavigateToNext = { navController.navigate(AddStoreRoute.STORE_DETAIL) }
                )
            }
            composable(AddStoreRoute.STORE_DETAIL) {
                StoreDetailScreen(
                    viewModel = viewModel,
                    onNavigateToNext = { navController.navigate(AddStoreRoute.COMPLETION) }
                )
            }
            composable(AddStoreRoute.COMPLETION) {
                CompletionScreen(
                    viewModel = viewModel,
                    onComplete = onComplete
                )
            }
        }
    }
}

@Composable
private fun AddStoreTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로 가기"
                )
            }
        },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        elevation = 0.dp
    )
}
