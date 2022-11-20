plugins {
    kotlin("jvm")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core.jvm)
}
