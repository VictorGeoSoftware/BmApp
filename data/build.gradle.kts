plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.briel.marnisos.brielapp.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 27
        consumerProguardFiles("consumer-rules.pro")
    }

    flavorDimensions += "environment"
    productFlavors {
        create("local") {
            dimension = "environment"
            // Requires: adb reverse tcp:8081 tcp:8081 (run once per device/emulator boot).
            // We use 127.0.0.1 over the adb tunnel instead of the emulator's 10.0.2.2 alias
            // because a full-tunnel VPN on the host breaks 10.0.2.2 -> host loopback. The adb
            // reverse tunnel rides over adb (not the IP network), so it survives the VPN and
            // also works on physical devices. Use 127.0.0.1 (not localhost, which may resolve
            // to IPv6 ::1, which adb reverse does not bind).
            buildConfigField("String", "API_BASE_URL", "\"http://127.0.0.1:8081/api/v1\"")
        }
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"http://217.154.181.175:9081/api/v1\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"http://217.154.181.175:8081/api/v1\"")
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
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    // modules
    implementation(project(":domain"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Google Sign-In (Credential Manager)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.googleid)

    // Ktor client
    api(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Koin DI
    implementation(libs.koin.core)

    // Kotlinx serialization
    implementation(libs.kotlinx.serialization.json)

    // Test dependencies
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlin.test)
}
