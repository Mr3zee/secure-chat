import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    val state by mutableStateOf(0)

    renderComposable(rootElementId = "root") {
        Div {
            Text("Page state: $state, 0 - not connected to server, 1 - connected")
        }
    }
}
