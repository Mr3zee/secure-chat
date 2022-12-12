package com.example.secure.chat.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val Ui: CoroutineScope = CoroutineScope(Dispatchers.Default)

fun <T> launch(scope: CoroutineScope, body: suspend () -> T): Job = with(scope) {
    launch {
        body()
    }
}

fun <T> safeLaunch(scope: CoroutineScope, body: suspend () -> T): Job  {
    return launch(scope) {
        try {
            body()
        } catch (e : dynamic) {
            console.warn("Exception in coroutine launch", e)
        }
    }
}
