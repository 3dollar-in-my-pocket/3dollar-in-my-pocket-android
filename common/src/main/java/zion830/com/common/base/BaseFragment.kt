package zion830.com.common.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import zion830.com.common.BR

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int
) : Fragment(layoutResId) {
    protected abstract val binding: B

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        initView()
    }

    open fun initBinding() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
    }

    abstract fun initView()
}