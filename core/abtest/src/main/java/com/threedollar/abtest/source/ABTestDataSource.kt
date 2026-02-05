package com.threedollar.abtest.source

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.threedollar.abtest.model.ABTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

interface ABTestDataSource {
    val activatedABTests: List<ABTest>

    suspend fun activate()
}

internal class ABTestDataSourceImpl @Inject constructor() : ABTestDataSource {

    private val firebaseRemoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    private val _activatedABTests = MutableStateFlow<List<ABTest>>(emptyList())

    override val activatedABTests: List<ABTest>
        get() = _activatedABTests.value

    override suspend fun activate() {
        if (!fetchAndActivate()) {
            return
        }

        _activatedABTests.update { fetchActivatedABTests() }
    }

    private fun fetchActivatedABTests() = firebaseRemoteConfig
        .getKeysByPrefix(PREFIX)
        .mapNotNull { key ->
            runCatching {
                ABTest(
                    key = key,
                    value = firebaseRemoteConfig.getString(key)
                )
            }.getOrNull()
        }

    private suspend fun fetchAndActivate(): Boolean = suspendCancellableCoroutine { cont ->
        firebaseRemoteConfig
            .fetchAndActivate()
            .addOnSuccessListener {
                if (cont.isActive) {
                    cont.resume(true)
                }
            }
            .addOnFailureListener {
                if (cont.isActive) {
                    cont.resume(false)
                }
            }
    }

    companion object {
        private const val PREFIX = "abtest"
    }
}
