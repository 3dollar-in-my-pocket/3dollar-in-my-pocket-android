package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel

@Composable
fun CompletionScreen(
    viewModel: AddStoreViewModel,
    onComplete: () -> Unit,
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
                text = "작성 완료 화면",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )

            Text(
                text = "입력한 정보를 최종 확인하고 제출하는 화면입니다",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFFF5C43)
                )
            ) {
                Text("제보 완료", color = Color.White)
            }
        }
    }
}
