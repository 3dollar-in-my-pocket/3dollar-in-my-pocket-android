package zion830.com.common.ext

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.updateStatus(isValid: Boolean, strResId: Int) {
    if (isValid) {
        this.hideMessage()
    } else {
        this.showMessage(strResId)
    }
}

fun TextInputLayout.showMessage(strResId: Int) {
    this.apply {
        error = context.getString(strResId)
        requestFocus()
    }
}

fun TextInputLayout.hideMessage() {
    this.error = null
}