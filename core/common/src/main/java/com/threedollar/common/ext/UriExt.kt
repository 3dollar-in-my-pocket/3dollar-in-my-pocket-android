package com.threedollar.common.ext

import android.net.Uri

fun Uri?.orEmpty(): Uri = this ?: Uri.EMPTY
