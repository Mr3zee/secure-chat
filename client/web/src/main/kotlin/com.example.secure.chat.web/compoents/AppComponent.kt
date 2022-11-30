package com.example.secure.chat.web.compoents

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.models.AppModel
import org.jetbrains.compose.web.dom.Text

@Composable
fun AppComponent(
    model: AppModel,
    body: @Composable () -> Unit = {}
) {
    Text("Page state: ${model.state.value}, 0 - not connected to server, 1 - connected")

    body()
}
