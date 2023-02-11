import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0" apply false
    id("org.jmailen.kotlinter") version "3.9.0" apply false
    id("com.github.gmazzo.buildconfig") version "3.0.3" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

allprojects {
    group = "suwayomi"

    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://maven.google.com/")
        maven("https://jitpack.io")
        maven("https://dl.google.com/dl/android/maven2/")
        maven("https://repo1.maven.org/maven2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

val projects = listOf(
        project(":AndroidCompat"),
        project(":AndroidCompat:Config"),
        project(":server")
)

configure(projects) {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    dependencies {
        // Kotlin
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))
        testImplementation(kotlin("test"))

        // coroutines
        val coroutinesVersion = "1.6.4"
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

        val kotlinSerializationVersion = "1.4.0"
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinSerializationVersion")


        // Dependency Injection
        implementation("org.kodein.di:kodein-di-conf-jvm:7.11.0")

        // Logging
        implementation("org.slf4j:slf4j-api:1.7.36")
        implementation("ch.qos.logback:logback-classic:1.2.11")
        implementation("io.github.microutils:kotlin-logging:2.1.21")

        // ReactiveX
        implementation("io.reactivex:rxjava:1.3.8")
        implementation("io.reactivex:rxkotlin:1.0.0")
        implementation("com.jakewharton.rxrelay:rxrelay:1.2.0")

        // JSoup
        implementation("org.jsoup:jsoup:1.15.3")

        // dependency of :AndroidCompat:Config
        implementation("com.typesafe:config:1.4.2")
        implementation("io.github.config4k:config4k:0.4.2")

        // dex2jar
        val dex2jarVersion = "v59"
        implementation("com.github.ThexXTURBOXx.dex2jar:dex-translator:$dex2jarVersion")
        implementation("com.github.ThexXTURBOXx.dex2jar:dex-tools:$dex2jarVersion")

        // APK parser
        implementation("net.dongliu:apk-parser:2.6.10")
    }
}
