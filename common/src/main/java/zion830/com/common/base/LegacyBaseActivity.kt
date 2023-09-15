package zion830.com.common.base

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.threedollar.common.base.BaseViewModel
import zion830.com.common.BR
import zion830.com.common.ext.showSnack

abstract class LegacyBaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutId)
        initBinding()
        initView()

        viewModel.msgTextId.observe(this) {
            if (it >= 0) {
                binding.root.showSnack(it)
            }
        }
    }

    open fun initBinding() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
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