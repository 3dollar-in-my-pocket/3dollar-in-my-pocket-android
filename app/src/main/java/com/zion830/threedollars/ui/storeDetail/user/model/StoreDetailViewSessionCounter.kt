package com.zion830.threedollars.ui.storeDetail.user.model

object StoreDetailViewSessionCounter {
    private val countsByStoreId = mutableMapOf<Int, Int>()

    fun increment(storeId: Int): Int {
        val count = (countsByStoreId[storeId] ?: 0) + 1
        countsByStoreId[storeId] = count
        return count
    }

    fun clear() {
        countsByStoreId.clear()
    }
}
