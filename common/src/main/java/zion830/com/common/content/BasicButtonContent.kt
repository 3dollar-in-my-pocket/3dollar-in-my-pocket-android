package zion830.com.common.content

import android.view.View

data class BasicButtonContent(
    val text: String? = "",
    val onClick: (view: View) -> Unit
)