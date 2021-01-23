package zion830.com.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.observe
import zion830.com.common.BR
import zion830.com.common.ext.showSnack

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
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
            binding.root.showSnack(it)
        }
    }

    open fun initBinding() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
    }

    abstract fun initView()
}