plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tomorrow.projectStartup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tomorrow.androidprojectstartup"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
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
    implementation(project(":Navigation"))
    implementation(libraries.compose.ui)
    implementation(libraries.compose.foundation)
    implementation(libraries.compose.ui.graphics)
    implementation(libraries.compose.ui.tooling.preview)
    implementation(libraries.compose.material)
    implementation(libraries.compose.material3)
    implementation(libraries.lifecycle.viewmodel.compose)
    implementation(libraries.compose.ui.tooling)
    implementation(libraries.compose.foundation)
    implementation(libraries.compose.foundation.layout)
    implementation(libraries.coil.kt.compose)
    implementation(libraries.compose.bom)
    implementation(libraries.androidx.activity.compose)
    implementation(libraries.compose.navigation)
    implementation(libraries.navigation.runtime)
    implementation(kotlin("reflect"))
}