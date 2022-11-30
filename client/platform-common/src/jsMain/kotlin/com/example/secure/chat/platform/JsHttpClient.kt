package com.example.secure.chat.platform

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

actual object ClientHttpEngine {
    actual val engine: HttpClientEngineFactory<HttpClientEngineConfig> = Js
}
