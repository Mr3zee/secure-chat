plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(libs.kotlin.plugin)
    }
}


dependencies {
    implementation(libs.kotlin.plugin)
}
