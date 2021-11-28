import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask
import java.io.BufferedReader

plugins {
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jmailen.kotlinter") version "3.4.3"
    id("de.fuerstenau.buildconfig") version "1.1.8"
}

dependencies {
    // okhttp
    val okhttpVersion = "4.9.1" // version is locked by Tachiyomi extensions
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttpVersion")
    implementation("com.squareup.okio:okio:2.10.0")


    // dependencies of Tachiyomi extensions, some are duplicate, keeping it here for reference
    implementation("com.github.inorichi.injekt:injekt-core:65b0440")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")


    // Source models and interfaces from Tachiyomi 1.x
    // using source class from tachiyomi commit 9493577de27c40ce8b2b6122cc447d025e34c477 to not depend on tachiyomi.sourceapi
//    implementation("tachiyomi.sourceapi:source-api:1.1")

    // AndroidCompat
    implementation(project(":AndroidCompat"))
    implementation(project(":AndroidCompat:Config"))

    // uncomment to test extensions directly
//    implementation(fileTree("lib/"))

    // Testing
    testImplementation(kotlin("test-junit5"))
}

val MainClass = "suwayomi.tachidesk.MainKt"
application {
    mainClass.set(MainClass)
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
        }
    }
}

// should be bumped with each stable release
val inspectorVersion = "v0.6.0"

// counts commit count on master
val inspectorRevision = runCatching {
    System.getenv("ProductRevision") ?: Runtime
        .getRuntime()
        .exec("git rev-list HEAD --count")
        .let { process ->
            process.waitFor()
            val output = process.inputStream.use {
                it.bufferedReader().use(BufferedReader::readText)
            }
            process.destroy()
            "r" + output.trim()
        }
}.getOrDefault("r0")

buildConfig {
    clsName = "BuildConfig"
    packageName = "suwayomi.server"

    buildConfigField("String", "NAME", rootProject.name)
    buildConfigField("String", "VERSION", inspectorVersion)
    buildConfigField("String", "REVISION", inspectorRevision)
}

tasks {
    shadowJar {
        manifest {
            attributes(
                    mapOf(
                            "Main-Class" to MainClass,
                            "Implementation-Title" to rootProject.name,
                            "Implementation-Vendor" to "The Tachiyomi Open Source Project",
                            "Specification-Version" to inspectorVersion,
                            "Implementation-Version" to inspectorRevision
                    )
            )
        }
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(inspectorVersion)
        archiveClassifier.set(inspectorRevision)
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xopt-in=kotlinx.coroutines.InternalCoroutinesApi",
                    "-Xopt-in=kotlin.io.path.ExperimentalPathApi",
            )
        }
    }

    test {
        useJUnit()
    }

    withType<ShadowJar> {
        destinationDirectory.set(File("$rootDir/server/build"))
        dependsOn("formatKotlin", "lintKotlin")
    }

    named("run") {
        dependsOn("formatKotlin", "lintKotlin")
    }

    withType<LintTask> {
        source(files("src/kotlin"))
    }

    withType<FormatTask> {
        source(files("src/kotlin"))
    }

    withType<ProcessResources> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}
