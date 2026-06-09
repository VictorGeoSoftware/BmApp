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
            // 10.0.2.2 is the Android emulator alias for the host machine's localhost.
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8081/api/v1\"")
        }
        create("dev") {
            dimension = "environment"
            // TEMP: pointing at prod backend until QA backend (port 9081) is deployed.
            // TODO: switch back to "http://217.154.181.175:9081/api/v1" once QA is up (see MULTI_ENV_DEPLOYMENT.md).
            buildConfigField("String", "API_BASE_URL", "\"http://217.154.181.175:8081/api/v1\"")
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
