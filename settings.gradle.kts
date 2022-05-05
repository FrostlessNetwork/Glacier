rootProject.name = "glacier"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.jpenilla.xyz/snapshots/")
    }
}

includeBuild("./libs/BukkitAPI")