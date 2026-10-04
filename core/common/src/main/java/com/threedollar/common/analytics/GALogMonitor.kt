package com.threedollar.common.analytics

import android.os.Bundle

/**
 * 개발 빌드의 GA 로그 뷰어가 앱이 보내는 GA 이벤트를 함께 받아보는 통로.
 * release 빌드에서는 아무도 [listener]를 등록하지 않아 전송에 영향이 없다.
 */
object GALogMonitor {
    @Volatile
    var listener: ((eventName: String, parameters: Bundle) -> Unit)? = null

    fun record(eventName: String, parameters: Bundle) {
        listener?.invoke(eventName, Bundle(parameters))
    }
}
