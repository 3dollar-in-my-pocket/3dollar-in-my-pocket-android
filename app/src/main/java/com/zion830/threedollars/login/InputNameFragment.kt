package com.zion830.threedollars.login

import androidx.fragment.app.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentLoginNameBinding
import zion830.com.common.base.BaseFragment

class InputNameFragment : BaseFragment<FragmentLoginNameBinding, LoginViewModel>(R.layout.fragment_login_name) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {

    }

    companion object {

        fun getInstance() = InputNameFragment()
    }
}