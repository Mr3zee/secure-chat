@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    useJs()
}

dependencies {
    commonMainApi(project(":common"))

    commonMainApi(libs.decompose)
}
