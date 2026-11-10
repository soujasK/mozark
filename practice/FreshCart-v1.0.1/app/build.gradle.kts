plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.fromprompt.freshcart"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.fromprompt.freshcart"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.1"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}
