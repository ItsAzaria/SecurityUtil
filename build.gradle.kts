group = "me.moe"
version = Versions.BotVersion
description = "securityutil"

object Versions {
    const val BotVersion = "1.0.0"

    const val DiscordKt = "0.23.0-SNAPSHOT"
    const val Fuel = "2.3.1"
    const val GithubAPI = "1.301"
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DiscordKt}")
    implementation("com.github.kittinunf.fuel:fuel-gson:${Versions.Fuel}")
    implementation("com.github.kittinunf.fuel:fuel:${Versions.Fuel}")
    implementation("org.kohsuke:github-api:${Versions.GithubAPI}")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    shadowJar {
        archiveFileName.set("SecurityUtil.jar")
        manifest {
            attributes(
                "Main-Class" to "me.moe.securityutil.MainKt"
            )
        }
    }
}