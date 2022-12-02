@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvm()
    useJs()
}

dependencies {
    commonMainApi(libs.kotlin.stdlib)
    commonMainApi(libs.kotlin.reflect)
    commonMainApi(libs.kotlinx.serialization.json)
    commonMainApi(libs.ktor.client.core)
    commonMainApi(libs.ktor.client.content.negotiation)
    commonMainApi(libs.ktor.serialization.kotlinx.json)
    commonMainApi(libs.kotlinx.coroutines.core)
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.koin.core)
}
