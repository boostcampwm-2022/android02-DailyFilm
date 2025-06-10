plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 36
    namespace = "com.gowtham.library"

    packagingOptions {
        pickFirst("lib/x86/libc++_shared.so")
        pickFirst("lib/x86_64/libc++_shared.so")
        pickFirst("lib/armeabi-v7a/libc++_shared.so")
        pickFirst("lib/arm64-v8a/libc++_shared.so")
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 36
        ndkVersion = "22.1.7171670"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("assets", "src\\main\\assets")
            jniLibs.srcDirs(listOf("../path/to/libs"))
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(files("libs/mobile-ffmpeg-min-gpl.aar"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.exoplayer)
    implementation(libs.glide)
    ksp(libs.glide.compiler)
    implementation(libs.localization)
    implementation(libs.gson)
    implementation(libs.android.lottie)
}
