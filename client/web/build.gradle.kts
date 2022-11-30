@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("js")
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    useJs {
        commonWebpackConfig {
            cssSupport {
                enabled = true
            }

            val proxies = devServer?.proxy ?: mutableMapOf()
            listOf(
                "/api",
                "/images",
            ).forEach {
                proxies[it] = "http://localhost:8080"
            }

            devServer = devServer?.copy(
                port = 3000,
                proxy = proxies
            )
        }
    }
}

dependencies {
    implementation(project(":client:platform-common"))

    implementation(libs.kotlin.stdlib.js)

    implementation(enforcedPlatform(libs.kotlin.wrappers.bom))
    implementation(libs.kw.web)
    implementation(libs.kw.browser)

    implementation(libs.ktor.client.js)
    implementation(libs.ktor.client.websockets)
    implementation(libs.kotlinx.coroutines.core.js)

    implementation(compose.web.core)
    implementation(compose.runtime)
}
