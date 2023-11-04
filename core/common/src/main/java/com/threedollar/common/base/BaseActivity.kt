package com.threedollar.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.threedollar.common.ext.showSnack

abstract class BaseActivity<B : ViewBinding, VM : BaseViewModel>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity() {

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        initView()

        viewModel.msgTextId.observe(this) {
            if (it >= 0) {
                binding.root.showSnack(it)
            }
        }
    }

    abstract fun initView()

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val v = currentFocus
        val ret = super.dispatchTouchEvent(event)

        if (v is EditText) {
            val location = IntArray(2)

            currentFocus?.let { w ->
                w.getLocationOnScreen(location)
                val x = event.rawX + w.left - location[0]
                val y = event.rawY + w.top - location[1]

                if (event.action == MotionEvent.ACTION_UP && (x < w.left || x >= w.right || y < w.top || y > w.bottom)) {
                    hideKeyboard()
                }
            }
        }

        return ret
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
    }
}