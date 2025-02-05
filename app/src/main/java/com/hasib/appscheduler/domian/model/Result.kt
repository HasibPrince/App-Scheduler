package com.hasib.appscheduler.domian.model

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    class Success<out T>(val data: T) : Result<T>()
    class Error(val e: Throwable) : Result<Nothing>()
}

fun <T> Result<T>.doOnLoading(block: () -> Unit) {
    if (this is Result.Loading) {
        block()
    }
}

suspend fun <T> Result<T>.doOnSuccess(block: suspend (T) -> Unit) {
    if (this is Result.Success) {
        block(data)
    }
}

fun <T> Result<T>.doOnError(block: (error: Throwable) -> Unit) {
    if (this is Result.Error) {
        block(e)
    }
}