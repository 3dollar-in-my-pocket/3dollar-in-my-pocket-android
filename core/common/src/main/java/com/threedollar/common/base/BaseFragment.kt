package com.threedollar.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

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
        initFirebaseAnalytics()
        initView()
    }

    fun setFirebaseAnalyticsLogEvent(className: String, screenName: String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun initView()

    abstract fun initFirebaseAnalytics()
}