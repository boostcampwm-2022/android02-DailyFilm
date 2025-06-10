package com.dailyfilm.buildlogic.primitive

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import kotlin.apply
import kotlin.collections.forEach
import kotlin.collections.plusAssign
import kotlin.run

@Suppress("EnumEntryName")
enum class FlavorDimension {
    version
}

@Suppress("EnumEntryName")
enum class DailyFilmFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
) {
    dev(FlavorDimension.version, applicationIdSuffix = ".dev"),
    stable(FlavorDimension.version),
}

internal fun configureProductFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: DailyFilmFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.version.name
        productFlavors {
            DailyFilmFlavor.values().forEach { flavor ->
                create(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        flavor.applicationIdSuffix?.run {
                            applicationIdSuffix = this
                        }
                    }
                }
            }
        }
    }
}