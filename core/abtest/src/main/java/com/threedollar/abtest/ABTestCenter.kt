package com.threedollar.abtest

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.threedollar.abtest.model.ABTest
import com.threedollar.abtest.source.ABTestDataSource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ABTestCenter {
    val activatedABTests: List<ABTest>

    fun init()
}

internal class ABTestCenterImpl @Inject constructor(
    private val dataSource: ABTestDataSource
) : ABTestCenter, DefaultLifecycleObserver {

    private val lifecycleOwner: LifecycleOwner = ProcessLifecycleOwner.get()

    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics

    private val coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, t ->
        crashlytics.recordException(t)
    }

    private var fetchJob: Job? = null

    override val activatedABTests: List<ABTest>
        get() = dataSource.activatedABTests

    override fun init() {
        lifecycleOwner.lifecycle.addObserver(this)
        launchFetchIfNeeded()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        launchFetchIfNeeded()
    }

    private fun launchFetchIfNeeded() {
        if (fetchJob != null) {
            return
        }

        lifecycleOwner.lifecycleScope.launch(coroutineContext) {
            dataSource.activate()
        }.also {
            fetchJob = it
        }.invokeOnCompletion {
            fetchJob = null
        }
    }
}
