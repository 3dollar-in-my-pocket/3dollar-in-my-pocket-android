package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel

@Composable
fun MenuCategoryScreen(
    viewModel: AddStoreViewModel,
    onNavigateToNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "음식 카테고리 선택 화면",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )

            Text(
                text = "간식, 식사 등 음식 카테고리를 선택하는 화면입니다\n최대 10개 선택 가능",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onNavigateToNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            ) {
                Text("다음")
            }
        }
    }
}
