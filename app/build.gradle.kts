import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.eoi.jagadzikir"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.eoi.jagadzikir"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Test builds only. A real upload key replaces this before publishing.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = false
    }

    // Leave out files the app never uses, to keep the APK tiny.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    packaging {
        resources {
            excludes += listOf("kotlin/**", "META-INF/*.kotlin_module", "META-INF/*.version", "DebugProbesKt.bin")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
