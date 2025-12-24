package com.threedollar.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.ext.showSnack

abstract class BaseActivity<B : ViewBinding, VM : BaseViewModel>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity() {

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = insets.top,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        initView()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        viewModel.msgTextId.observe(this) {
            if (it >= 0) {
                binding.root.showSnack(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.screenName != ScreenName.EMPTY) {
            sendScreenView(viewModel.screenName)
        }
        initFirebaseAnalytics()
    }

    abstract fun initView()

    @Deprecated(
        message = "No longer needed. ViewModel.screenName is used automatically",
        replaceWith = ReplaceWith("")
    )
    open fun initFirebaseAnalytics() {
        // Override in legacy screens if needed
        // New screens should set ViewModel.screenName instead
    }
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

    @Deprecated(
        message = "Use sendScreenView(ScreenName) instead",
        replaceWith = ReplaceWith("sendScreenView(screen)")
    )
    fun setFirebaseAnalyticsLogEvent(className: String, screenName: String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }

    fun sendScreenView(screen: ScreenName, extraParameters: Map<ParameterName, Any> = emptyMap()) {
        LogManager.sendPageView(screen, this::class.java.simpleName, extraParameters)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
    }

    fun isBindingInitialized() = ::binding.isInitialized

    // 밝은 시스템바 (어두운 아이콘/텍스트) - 흰색 배경용
    protected fun setLightSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    // 어두운 시스템바 (밝은 아이콘/텍스트) - 검은색 배경용
    protected fun setDarkSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}