plugins {
    java
    id("io.freefair.lombok") version "6.3.0"
}

group = "network.frostless"
version = "0.0.1"

repositories {
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    // FrostCore
    compileOnly("network.frostless:FrostCore:0.0.1")
    compileOnly("network.frostless:BukkitAPI:0.0.1")

    compileOnly(fileTree(mapOf("dir" to "extern", "include" to listOf("*.jar"))))
}