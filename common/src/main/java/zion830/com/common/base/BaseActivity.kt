package zion830.com.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.BR

@AndroidEntryPoint
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
    }

    open fun initBinding() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
    }

    abstract fun initView()
}