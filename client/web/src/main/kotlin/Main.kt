import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.example.secure.chat.web.compoents.AppComponent
import com.example.secure.chat.web.models.AppModel
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Document


fun main() {
    val lifecycle = LifecycleRegistry()

    val rootModel = AppModel(componentContext = DefaultComponentContext(lifecycle))

    lifecycle.attachToDocument()

    renderComposable(rootElementId = "root") {
        AppComponent(rootModel)
    }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (document.visibilityState == "visible") {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}

private val Document.visibilityState: String
    get() = asDynamic().visibilityState.unsafeCast<String>()