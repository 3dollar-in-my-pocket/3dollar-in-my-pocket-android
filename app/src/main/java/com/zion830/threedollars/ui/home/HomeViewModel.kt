package com.zion830.threedollars.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import zion830.com.common.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text
}