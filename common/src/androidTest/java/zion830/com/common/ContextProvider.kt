package zion830.com.common

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.jetbrains.annotations.TestOnly

/*
 * Created by yunji on 05/05/2020
 */
object ContextProvider {

    @TestOnly
    fun requireContext(): Context = InstrumentationRegistry.getInstrumentation().context
}