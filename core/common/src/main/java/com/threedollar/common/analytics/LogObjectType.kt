package com.threedollar.common.analytics

enum class LogObjectType(val value: String) {
    BUTTON("button"),
    CARD("card"),
    MARKER("marker"),
    BANNER("banner"),
    TAB("tab"),
    FILTER("filter"),
    FIELD("field"),
    REVIEW("review"),
    MEDAL("medal"),
    MENU("menu");

    override fun toString(): String = value
}
