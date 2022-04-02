plugins {
    application
    kotlin("plugin.serialization")
}

dependencies {
    // Android stub library
    implementation(fileTree("lib/"))

    // XML
    compileOnly("xmlpull:xmlpull:1.1.3.4a")

    // Config API
    implementation(project(":AndroidCompat:Config"))

    // APK sig verifier
    compileOnly("com.android.tools.build:apksig:7.1.2")

    // AndroidX annotations
    compileOnly("androidx.annotation:annotation:1.3.0")

    // substitute for duktape-android
    implementation("org.mozilla:rhino-runtime:1.7.14") // slimmer version of 'org.mozilla:rhino'
    implementation("org.mozilla:rhino-engine:1.7.14") // provides the same interface as 'javax.script' a.k.a Nashorn

    // Kotlin wrapper around Java Preferences, makes certain things easier
    val multiplatformSettingsVersion = "0.8.1"
    implementation("com.russhwolf:multiplatform-settings-jvm:$multiplatformSettingsVersion")
    implementation("com.russhwolf:multiplatform-settings-serialization-jvm:$multiplatformSettingsVersion")

    // Android version of SimpleDateFormat
    implementation("com.ibm.icu:icu4j:70.1")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}
