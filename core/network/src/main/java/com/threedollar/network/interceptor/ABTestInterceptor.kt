package com.threedollar.network.interceptor

import com.threedollar.abtest.ABTestCenter
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

interface ABTestInterceptor : Interceptor

internal class ABTestInterceptorImpl @Inject constructor(
    private val abTestCenter: ABTestCenter
): ABTestInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(HEADER, buildExperimentContextHeader())
            .build()

        return chain.proceed(request)
    }

    private fun buildExperimentContextHeader(): String = abTestCenter
        .activatedABTests
        .joinToString(",") { "${it.key}=${it.value}" }

    companion object {
        private const val HEADER = "Experiment-Context"
    }
}
