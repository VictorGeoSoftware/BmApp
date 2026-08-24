plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.briel.marnisos.brielapp.analytics"
    compileSdk = 36

    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    // Must mirror the flavours declared by :app and :data, otherwise variant
    // matching fails when :app resolves this module.
    flavorDimensions += "environment"
    productFlavors {
        // ANALYTICS_COLLECTION_ALLOWED — master switch per environment.
        // ANALYTICS_ALLOW_DEBUG_BUILDS — when false, only release builds may report.
        //
        // To validate events locally in Firebase DebugView, temporarily flip the dev
        // flavour's ANALYTICS_COLLECTION_ALLOWED to true and run:
        //   adb shell setprop debug.firebase.analytics.app com.briel.marnisos.powerapp.dev
        // Do not commit that flip.
        create("local") {
            dimension = "environment"
            buildConfigField("boolean", "ANALYTICS_COLLECTION_ALLOWED", "false")
            buildConfigField("boolean", "ANALYTICS_ALLOW_DEBUG_BUILDS", "true")
        }
        create("dev") {
            dimension = "environment"
            buildConfigField("boolean", "ANALYTICS_COLLECTION_ALLOWED", "false")
            buildConfigField("boolean", "ANALYTICS_ALLOW_DEBUG_BUILDS", "true")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("boolean", "ANALYTICS_COLLECTION_ALLOWED", "true")
            buildConfigField("boolean", "ANALYTICS_ALLOW_DEBUG_BUILDS", "false")
        }
    }

    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // Modules
    implementation(project(":domain"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
