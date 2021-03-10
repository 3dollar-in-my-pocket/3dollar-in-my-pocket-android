package com.zion830.threedollars.repository.model.response


class FaqTagResponse : ArrayList<FaqTag>() {

    fun getAllTag(): List<FaqTag> = listOf(FaqTag(-1, -1, "전체")) + this.sortedByDescending { it.displayOrder }
}