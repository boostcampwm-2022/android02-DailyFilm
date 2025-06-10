package com.dailyfilm.buildlogic.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")

        androidLibrary {
            configureAndroid()
            configureProductFlavors(this)
        }
    }
}