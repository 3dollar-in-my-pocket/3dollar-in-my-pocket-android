package com.threedollar.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName

abstract class BaseFragment<B : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.screenName != ScreenName.EMPTY) {
            sendPageView(viewModel.screenName)
        }
    }

    open fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any> = emptyMap()) {
        LogManager.sendPageView(screen, this::class.java.simpleName, extraParameters)
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun initView()
}