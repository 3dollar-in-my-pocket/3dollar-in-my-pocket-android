package com.zion830.threedollars.repository

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Exception) : Result<Nothing>()

    data class Progress(val isLoading: Boolean) : Result<Boolean>()
}