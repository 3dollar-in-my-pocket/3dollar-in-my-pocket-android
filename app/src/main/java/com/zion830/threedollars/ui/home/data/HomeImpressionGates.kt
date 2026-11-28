package com.zion830.threedollars.ui.home.data

class PendingSectionImpression<T : Any> {
    private var pending: T? = null

    fun offer(log: T?, isVisible: Boolean): T? {
        if (!isVisible) {
            pending = log
            return null
        }
        pending = null
        return log
    }

    fun flush(isVisible: Boolean): T? {
        if (!isVisible) return null
        return pending.also { pending = null }
    }

    fun clear() {
        pending = null
    }
}

class HomeCurationCardImpressionDedup {
    private val sentKeys = mutableSetOf<String>()

    fun shouldSend(carouselId: String, cardId: String): Boolean = sentKeys.add(key(carouselId, cardId))

    fun reset(carouselId: String) {
        sentKeys.removeAll { it.startsWith(prefix(carouselId)) }
    }

    fun resetAll() {
        sentKeys.clear()
    }

    private fun prefix(carouselId: String) = "$carouselId/"

    private fun key(carouselId: String, cardId: String) = prefix(carouselId) + cardId
}
