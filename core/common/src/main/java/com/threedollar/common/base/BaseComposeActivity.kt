package com.threedollar.common.base

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.threedollar.common.ext.showSnack

abstract class BaseComposeActivity<VM : BaseViewModel> : AppCompatActivity() {


    protected abstract val viewModel: VM

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val view = View(this)
        viewModel.msgTextId.observe(this) {
            if (it >= 0) {
                view.showSnack(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initFirebaseAnalytics()
    }

    abstract fun initFirebaseAnalytics()

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

    fun setFirebaseAnalyticsLogEvent(className: String, screenName: String?) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            screenName?.let {
                param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
    }
}