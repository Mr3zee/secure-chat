plugins {
    kotlin("jvm")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":server:base"))
    implementation(project(":server:domain"))
    implementation(libs.koin.jvm)
}
