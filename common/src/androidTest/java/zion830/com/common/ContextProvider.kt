package zion830.com.common

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.jetbrains.annotations.TestOnly

object ContextProvider {

    @TestOnly
    fun requireContext(): Context = InstrumentationRegistry.getInstrumentation().context
}