import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.dailyfilm.buildlogic"

repositories {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    val path = "com.dailyfilm.buildlogic"
    plugins {
        register("androidApplication") {
            id = "dailyfilm.android.application"
            implementationClass = "$path.primitive.AndroidApplicationPlugin"
        }
        register("androidCompose") {
            id = "dailyfilm.android.compose"
            implementationClass = "$path.primitive.AndroidComposePlugin"
        }
        register("androidKotlin") {
            id = "dailyfilm.android.kotlin"
            implementationClass = "$path.primitive.AndroidKotlinPlugin"
        }
        register("androidLibrary") {
            id = "dailyfilm.android.library"
            implementationClass = "$path.primitive.AndroidLibraryPlugin"
        }
        register("jvmLibrary") {
            id = "dailyfilm.jvm.library"
            implementationClass = "$path.primitive.JvmLibraryPlugin"
        }
    }
}