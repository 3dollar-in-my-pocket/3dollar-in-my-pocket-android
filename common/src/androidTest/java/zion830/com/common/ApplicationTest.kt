package zion830.com.common

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApplicationTest {

    @Test
    fun useInstrumentationContext() {
        val appContext = ContextProvider.requireContext()
        Assert.assertEquals("zion830.com.common.test", appContext.packageName)
    }

    @Test
    fun useAppContextNewRecommendedWay() {
        val appContext: Context = ApplicationProvider.getApplicationContext()
        assertThat("zion830.com.common.test", Matchers.`is`(appContext.packageName))
    }
}