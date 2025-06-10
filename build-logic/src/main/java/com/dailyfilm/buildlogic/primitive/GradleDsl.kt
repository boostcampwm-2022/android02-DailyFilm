package com.dailyfilm.buildlogic.primitive

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

internal fun DependencyHandlerScope.implementation(
    artifact: MinimalExternalModuleDependency,
) = add("implementation", artifact)

internal fun DependencyHandlerScope.implementationPlatform(
    artifact: MinimalExternalModuleDependency,
) = add("implementation", platform(artifact))

internal fun DependencyHandlerScope.implementationProject(
    path: String,
) = "implementation"(project(path))

internal fun DependencyHandlerScope.debugImplementation(
    artifact: MinimalExternalModuleDependency,
) = add("debugImplementation", artifact)

internal fun DependencyHandlerScope.coreLibraryDesugaring(
    artifact: MinimalExternalModuleDependency,
) = add("coreLibraryDesugaring", artifact)

internal fun DependencyHandlerScope.androidTestImplementationPlatform(
    artifact: MinimalExternalModuleDependency,
) = add("androidTestImplementation", platform(artifact))

internal fun DependencyHandlerScope.ksp(
    artifact: MinimalExternalModuleDependency,
) = "ksp"(artifact)