package com.zion830.threedollars.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow

class ToggleButtonGroupTableLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TableLayout(context, attrs), View.OnClickListener {
    private var activeRadioButton: RadioButton? = null

    val checkedButtonId: Int
        get() = activeRadioButton?.id ?: -1

    val value: String
        get() = activeRadioButton?.text.toString() ?: ""

    override fun onClick(v: View) {
        val rb = v as RadioButton
        if (activeRadioButton != null) {
            activeRadioButton!!.isChecked = false
        }
        rb.isChecked = true
        activeRadioButton = rb
    }

    override fun addView(
        child: View, index: Int,
        params: ViewGroup.LayoutParams
    ) {
        super.addView(child, index, params)
        setChildrenOnClickListener(child as TableRow)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        super.addView(child, params)
        setChildrenOnClickListener(child as TableRow)
    }

    private fun setChildrenOnClickListener(tr: TableRow) {
        for (i in 0 until tr.childCount) {
            val button = tr.getChildAt(i)
            (button as? RadioButton)?.setOnClickListener(this)
        }
    }
}