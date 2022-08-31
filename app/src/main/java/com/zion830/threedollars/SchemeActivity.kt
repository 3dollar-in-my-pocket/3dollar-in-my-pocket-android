package com.zion830.threedollars

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder

class SchemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink()
    }

    private fun handleDeepLink() {
        val deepLinkUri = intent.data
        val deepLinkIntent = deepLinkUri?.let {
            DeepLinkInfo.invoke(deepLinkUri).getIntent(this, it)
        } ?: DeepLinkInfo.getMainIntent(this)

        if (isTaskRoot) {
            TaskStackBuilder.create(this).apply {
                if (needAddMainForParent(deepLinkIntent)) {
                    addNextIntentWithParentStack(DeepLinkInfo.getMainIntent(this@SchemeActivity))
                }
                addNextIntent(deepLinkIntent)
            }.startActivities()

        } else {
            startActivity(deepLinkIntent)
        }

        finish()
    }

    private fun needAddMainForParent(intent: Intent): Boolean =
        when (intent.component?.className) {
            MainActivity::class.java.name -> false
            else -> true
        }
}