package com.zion830.threedollars.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

inline fun <T> retrofitCallback(
    crossinline fn: (Response<T>?, Throwable?) -> Unit
) = object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        fn(response, null)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        fn(null, t)
    }
}