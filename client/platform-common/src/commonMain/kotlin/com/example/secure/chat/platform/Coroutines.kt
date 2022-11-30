package com.example.secure.chat.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Ui: CoroutineScope = CoroutineScope(Dispatchers.Main)

fun <T> launch(scope: CoroutineScope, body: suspend () -> T) {
    with(scope) {
        launch {
            body()
        }
    }
}
