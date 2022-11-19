@file:Suppress("unused")

package utils

import csstype.PropertiesBuilder
import dom.Element
import emotion.css.cx
import kotlinx.js.jso
import react.PropsWithChildren
import react.PropsWithClassName
import react.dom.html.HTMLAttributes

external interface StyledProps : PropsWithClassName, PropsWithChildren

fun StyledProps.withCss(block: PropertiesBuilder.() -> Unit) {
    className = cx(className, emotion.css.css(jso(block)))
}

fun <T : Element> HTMLAttributes<T>.withCss(block: PropertiesBuilder.() -> Unit) {
    className = cx(className, emotion.css.css(jso(block)))
}
