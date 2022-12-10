rootProject.name = "secure-chat"

include(
    "common",

    "client",
    "client:platform-common",
    "client:web",

    "server:base",
    "server:domain",
    "server:core",
    "server:web",
    "server:application",
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
