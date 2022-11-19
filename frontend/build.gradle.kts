@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("js")
    alias(libs.plugins.kotlin.plugin.serialization)
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
    implementation(project(":common"))
    implementation(libs.kotlin.stdlib.js)
    implementation(enforcedPlatform(libs.kotlin.wrappers.bom))
    implementation(libs.react)
    implementation(libs.react.dom)
    implementation(libs.emotion)
    implementation(libs.ktor.client.js)
    implementation(libs.kotlinx.coroutines.core.js)
}
