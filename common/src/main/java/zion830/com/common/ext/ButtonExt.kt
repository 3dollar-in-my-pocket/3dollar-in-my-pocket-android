package zion830.com.common.ext

import android.graphics.Color
import android.view.View
import android.widget.Button
import zion830.com.common.content.BasicButtonContent

fun Button.setUsableWithColor(
    usable: Boolean,
    usableColorResId: Int = Color.BLACK,
    disableColorResId: Int = context.getColor(android.R.color.darker_gray)
) {
    isEnabled = usable
    setTextColor(if (usable) usableColorResId else disableColorResId)
}

fun Button.bindBasicContent(basicButtonContent: BasicButtonContent?) {
    if (basicButtonContent == null) {
        visibility = View.GONE
        return
    }

    text = basicButtonContent.text
    setOnClickListener { run(basicButtonContent.onClick) }
}