plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

rootProject.extra.apply {
    set("PUBLISH_ARTIFACT_ID", "appupdate")
}

apply(from = "$rootDir/scripts/publish.gradle")


android {
    namespace = "com.tomorrow.appupdate"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
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
    implementation(libraries.google.play.updateKtx)
    implementation(libraries.google.play.update)
}