package com.example.secure.chat.platform

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

suspend fun <T> withBackoff(
    timeoutMillis: Long = 5_000,
    retryCount: Int = 5,
    body: suspend () -> T,
): Result<T> {
    var retry = 0

    var lastErr: Throwable? = null

    while (retry < retryCount) {
        try {
            return withTimeout(timeoutMillis) {
                Result.success(body())
            }
        } catch (e: Throwable) {
            console.warn(e)

            if (e !is TimeoutCancellationException) {
                throw e
            }

            lastErr = e

            retry++
        }
    }

    return Result.failure(lastErr ?: IllegalStateException("Backoff timed out"))
}

suspend fun <T> backoff(
    timeoutMillis: Long = 5_000,
    retryCount: Int = 5,
    body: suspend () -> T,
): T? {
    return withBackoff(timeoutMillis, retryCount, body).let {
        when {
            it.isFailure -> {
                console.error(it.exceptionOrNull())
                null
            }

            else -> it.getOrNull()
        }
    }
}