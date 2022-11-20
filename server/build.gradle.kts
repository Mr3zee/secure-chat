@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.io.ktor.plugin)
    application
    distribution
}

val ktorApplicationClassName = "com.example.auth.server.ApplicationKt"

application {
    mainClass.set(ktorApplicationClassName)
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":common"))
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors.jvm)
    implementation(libs.ktor.server.call.logging.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.logback.classic)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
}

val buildAndCopyFrontend = tasks.register<Copy>("buildAndCopyFrontend") {
    val frontendDist = project(":frontend").tasks.named("browserDistribution")
    dependsOn(frontendDist)
    from(frontendDist)
    into("${project.projectDir}/src/main/resources/static")
}

val prepareAppResources = tasks.register("prepareAppResources") {
    dependsOn(buildAndCopyFrontend)
    finalizedBy("processResources")
}

val buildApp = tasks.register("buildApp") {
    dependsOn(prepareAppResources)
    finalizedBy("build")
}

tasks.named("buildImage") {
    dependsOn(buildApp)
}

tasks.named("buildFatJar") {
    dependsOn(buildApp)
}

tasks.named("runDocker") {
    dependsOn(buildApp)
}

tasks.named<JavaExec>("run") {
    dependsOn(buildApp)
    classpath(tasks.named<Jar>("jar"))
}

ktor {
    docker {
        localImageName.set("secure-chat")
        imageTag.set("0.0.1-SNAPSHOT")
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_11)
    }
    fatJar {
        archiveFileName.set("server.jar")
    }
}
