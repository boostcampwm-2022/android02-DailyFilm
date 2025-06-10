package com.dailyfilm.buildlogic.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "org.jetbrains.kotlin.plugin.compose")

        android { buildFeatures.compose = true }

        dependencies {
            val composeBom = libs.library("androidx-compose-bom")
            implementationPlatform(composeBom)
            androidTestImplementationPlatform(composeBom)
            implementation(libs.library("androidx.compose.ui"))
            implementation(libs.library("androidx.activity.compose"))
            implementation(libs.library("androidx.compose.material3"))
            debugImplementation(libs.library("androidx.compose.ui.tooling"))
            implementation(libs.library("androidx.compose.ui.tooling.preview"))
        }
    }
}