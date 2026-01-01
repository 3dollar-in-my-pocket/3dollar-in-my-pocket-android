package com.zion830.threedollars.core.ui.component.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import base.compose.ColorWhite
import base.compose.Gray90
import base.compose.LocalAppShape
import com.threedollar.common.R as CommonR

@Composable
fun ConfirmAlertDialog(
    title: String,
    text: String,
    confirmButton: String = stringResource(CommonR.string.close),
    shape: Shape = LocalAppShape.current.medium,
    properties: DialogProperties = DialogProperties(),
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onConfirm,
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .background(ColorWhite, shape = shape)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    color = Gray90
                )
                VerticalSpacer(12)
            }
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    color = Gray90
                )
            }
            VerticalSpacer(16)
            Text(
                text = confirmButton,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                color = Gray90,
                modifier = Modifier
                    .clickable(onClick = onConfirm)
                    .wrapContentSize()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .align(Alignment.End)
            )
        }
    }
}
