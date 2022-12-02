package com.example.secure.chat.web.compose

import androidx.compose.runtime.State
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.Lifecycle

interface ModelContext : ComponentContext {
    fun <T> State<T>.subscribe(callback: (T) -> Unit): (T) -> Unit {
        val proxy = MutableValue(StateProxy(this.value))

        proxy.subscribe {
            callback(it.value)
        }

        return {
            proxy.value = StateProxy(it)
        }
    }

    private data class StateProxy<T>(val value: T)
}

class DefaultModelContext(lifecycle: Lifecycle) : ModelContext {
    private val componentContext = DefaultComponentContext(lifecycle)

    override val backHandler = componentContext.backHandler
    override val instanceKeeper = componentContext.instanceKeeper
    override val lifecycle = componentContext.lifecycle
    override val stateKeeper = componentContext.stateKeeper
}
