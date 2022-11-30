import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl


fun KotlinMultiplatformExtension.useJs() {
    js(IR) {
        binaries.executable()
        useJsInternal()
    }
}

fun KotlinJsProjectExtension.useJs(browserConfig: KotlinJsBrowserDsl.() -> Unit = {}) {
    js(IR) {
        binaries.executable()
        useJsInternal(browserConfig)
    }
}

private fun KotlinJsTargetDsl.useJsInternal(browserConfig: KotlinJsBrowserDsl.() -> Unit = {}) {
    browser {
        binaries.executable()
        browserConfig()
    }
}

