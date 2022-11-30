package com.example.secure.chat.web.models

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.example.secure.chat.platform.sendAsyncApiRequest

class AppModel(
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    var state = mutableStateOf(0)

    init {
        lifecycle.doOnCreate {
            sendAsyncApiRequest("healthcheck") {
                state.value = 1
            }
        }
    }
}
