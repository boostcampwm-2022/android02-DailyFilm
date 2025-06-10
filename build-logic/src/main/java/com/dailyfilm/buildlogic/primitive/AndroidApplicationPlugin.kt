package com.dailyfilm.buildlogic.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.application")

        androidApplication {
            configureAndroid()
            configureProductFlavors(this)
        }

        dependencies {
            implementation(libs.library("androidx.core.ktx"))
            implementation(libs.library("androidx.lifecycle.runtime.ktx"))
            implementation(libs.library("androidx.lifecycle.viewmodel.ktx"))
        }
    }
}