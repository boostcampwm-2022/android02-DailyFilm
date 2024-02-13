import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.boostcamp.dailyfilm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.boostcamp.dailyfilm"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0"

        val key = gradleLocalProperties(rootDir).getProperty("database.url") ?: ""
        buildConfigField("String", "DATABASE_URL", key)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(listOf("ko-rKR", "ko"))
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    // Android Studio Preview support
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation(libs.androidx.compose.runtime)
    // collectAsStateWithLifecycle()
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Compose Text Effect
    implementation(libs.extendedspans)

    // Activity
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
//    implementation(libs.androidx.hilt.common)
//    ksp(libs.androidx.hilt.compiler)
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    // Firebase Storage
    implementation(libs.firebase.storage.ktx)
    // Firebase Realtime Database
    implementation(libs.firebase.database.ktx)
    // Firebase Google Auth
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    // Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)
    implementation(libs.glide.compose)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.exoplayer)
    // Coordinator-layout
    implementation(libs.androidx.coordinatorlayout)
    // ffmpeg
    implementation(libs.android.video.trimmer)
    implementation(libs.mobile.ffmpeg.min.gpl)
    // lottie
    implementation(libs.android.lottie)
    implementation(libs.android.lottie.compose)
    // dataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    // keyboard
    implementation(libs.keyboard.visibility.event)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    // mockito
    testImplementation(libs.mockito.inline)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}