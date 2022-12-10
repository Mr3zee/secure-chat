package com.example.secure.chat.web.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.value.MutableValue
import kotlinx.atomicfu.atomic

interface Property<T> {
    val value: T

    fun subscribe(handler: (T) -> Unit): Long

    fun subscribeWithPrev(handler: (prev: T, new: T) -> Unit): Long

    fun unsubscribe(id: Long)

    fun fire()

    fun asState(): State<T>
}

class MutableProperty<T : Any?> internal constructor(initialValue: T) : Property<T> {
    private val oldValue = MutableValue(StateProxy(initialValue))
    private val currentValue = MutableValue(StateProxy(initialValue))

    private val atomicLong = atomic(0L)
    private val map = mutableMapOf<Long, (StateProxy<T>) -> Unit>()

    override var value: T
        get() = currentValue.value.value
        set(value) {
            currentValue.value = StateProxy(value)
        }

    override fun fire() {
        currentValue.value = currentValue.value
    }

    override fun subscribeWithPrev(handler: (prev: T, new: T) -> Unit): Long {
        val observer = { it: StateProxy<T> ->
            handler(oldValue.value.value, it.value)
        }
        return remember(observer)
    }

    override fun subscribe(handler: (T) -> Unit): Long {
        val observer = { it: StateProxy<T> ->
            handler(it.value)
        }
        return remember(observer)
    }

    private fun remember(observer: (StateProxy<T>) -> Unit): Long {
        val id = atomicLong.getAndIncrement()
        map[id] = observer
        currentValue.subscribe(observer)
        return id
    }

    override fun unsubscribe(id: Long) {
        map[id]?.let {
            currentValue.unsubscribe(it)
        }
    }

    private val state: State<T> by lazy {
        mutableStateOf(value).also { s ->
            subscribe {
                s.value = it
            }
        }
    }

    override fun asState(): State<T> {
        return state
    }
}

fun <T : Any?> mutableProperty(initialValue: T) = MutableProperty(initialValue)

private data class StateProxy<T>(val value: T)
