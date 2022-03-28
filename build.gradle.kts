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
    compileOnly("network.frostless:frostcore:0.0.1")
    compileOnly("network.frostless:BukkitAPI:0.0.1")
    compileOnly("network.frostless:FrostEntities:0.0.1")
    compileOnly("network.frostless:ServerAPI:0.0.1")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")

    compileOnly("io.lettuce:lettuce-core:6.1.6.RELEASE")

    // ORM
    compileOnly("com.j256.ormlite:ormlite-core:6.1")
    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1")

    // HikariCP
    compileOnly("com.zaxxer:HikariCP:5.0.0")

    compileOnly(fileTree(mapOf("dir" to "extern", "include" to listOf("*.jar"))))
}