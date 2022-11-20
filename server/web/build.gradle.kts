plugins {
    kotlin("jvm")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":server:core"))
    implementation(project(":common"))
    implementation(libs.koin.jvm)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.logback.classic)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.websockets)
}
