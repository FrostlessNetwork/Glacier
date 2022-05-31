plugins {
    java
    id("io.freefair.lombok") version "6.3.0"
    id("nu.studer.jooq") version "7.1.1"
    id("co.uzzu.dotenv.gradle") version "1.1.0"
}

group = "network.frostless"
version = "0.0.1"

repositories {
    maven {
        name = "FrostlessRepo"
        url = uri("https://repo.ricecx.cc/frostless")
        credentials(PasswordCredentials::class)
    }
    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven { url = uri("https://repo.rapture.pw/repository/maven-releases/") }
    maven { url = uri("https://repo.rapture.pw/repository/maven-snapshots/") }


    mavenCentral()
    mavenLocal()
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
    compileOnly("com.zaxxer:HikariCP:5.0.1")

    // Mc Dependencies
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.9.2")

    // Blitz Jar (just for nms mappings)
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT:remapped-mojang")

    compileOnly("com.grinderwolf:slimeworldmanager-api:2.8.0-SNAPSHOT")

    compileOnly("com.github.Revxrsal.Lamp:common:3.0.4")

    compileOnly("com.github.Revxrsal.Lamp:bukkit:3.0.4")
//    compileOnly(fileTree(mapOf("dir" to "extern", "include" to listOf("*.jar"))))
}
