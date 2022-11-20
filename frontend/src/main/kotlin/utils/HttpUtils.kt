package utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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

object HttpUtils {
    val client by lazy {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}

typealias HttpParametersBuilder = MutableMap<String, Any?>

@Suppress("unused")
inline fun <reified B> sendAsyncApiPostRequest(
    path: String,
    body: B? = null,
    crossinline parameters: HttpParametersBuilder.() -> Unit = {},
    crossinline headers: HeadersBuilder.() -> Unit = {},
    crossinline onError: suspend (HttpResponse) -> Unit = { it.logError() },
    crossinline onSuccess: suspend () -> Unit = {},
) = sendAsyncApiPostRequest<B, Unit>(path, body, parameters, headers, onError) { onSuccess() }

inline fun <reified B, reified R> sendAsyncApiPostRequest(
    path: String,
    body: B? = null,
    crossinline parameters: HttpParametersBuilder.() -> Unit = {},
    crossinline headers: HeadersBuilder.() -> Unit = {},
    crossinline onError: suspend (HttpResponse) -> Unit = { it.logError() },
    noinline onSuccess: (suspend (R) -> Unit)? = null
) = launch(Ui) {
    val response = HttpUtils.client.request("/api/$path") {
        method = HttpMethod.Post
        mutableMapOf<String, Any?>().apply(parameters).forEach { (key, value) ->
            parameter(key, value)
        }
        headers {
            headers()
        }
        body?.let {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    when (response.status.value) {
        in 200..299 -> onSuccess?.invoke(response.body())
        else -> onError(response)
    }
}

@Suppress("unused")
inline fun sendAsyncApiRequest(
    path: String,
    method: HttpMethod = HttpMethod.Get,
    crossinline parameters: HttpParametersBuilder.() -> Unit = {},
    crossinline headers: HeadersBuilder.() -> Unit = {},
    crossinline onError: suspend (HttpResponse) -> Unit = { it.logError() },
    crossinline onSuccess: suspend () -> Unit = {}
) = sendAsyncApiRequest<Unit>(path, method, parameters, headers, onError) { onSuccess() }

@Suppress("unused")
inline fun <reified R> sendAsyncApiRequest(
    path: String,
    method: HttpMethod = HttpMethod.Get,
    crossinline parameters: HttpParametersBuilder.() -> Unit = {},
    crossinline headers: HeadersBuilder.() -> Unit = {},
    crossinline onError: suspend (HttpResponse) -> Unit = { it.logError() },
    crossinline onSuccess: suspend (R) -> Unit = {}
) = launch(Ui) {
    val response = HttpUtils.client.request("/api/$path") {
        this.method = method
        mutableMapOf<String, Any?>().apply(parameters).forEach { (key, value) ->
            parameter(key, value)
        }
        this.headers {
            headers()
        }
    }

    when (response.status.value) {
        in 200..299 -> onSuccess(response.body())
        else -> onError(response)
    }
}

suspend fun HttpResponse.logError() {
    console.error("Received error on call to client, status: $status")
    bodyAsText().let {
        if (it.isNotBlank()) {
            console.error(it)
        }
    }
}
