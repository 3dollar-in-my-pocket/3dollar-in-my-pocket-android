package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import base.compose.Gray70
import base.compose.Pink500
import com.threedollar.common.R

@Composable
internal fun HomeCurationStatus(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEmpty: Boolean = false,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Pink500, strokeWidth = 2.dp)
                Text(stringResource(R.string.loading), color = Gray70)
            }
        } else {
            Text(stringResource(if (isEmpty) R.string.no_store else R.string.error_unknown_title), color = Gray70)
            onRetry?.let { retry ->
                TextButton(onClick = retry) { Text(stringResource(R.string.retry), color = Pink500) }
            }
        }
    }
}
