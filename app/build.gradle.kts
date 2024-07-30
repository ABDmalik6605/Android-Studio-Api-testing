plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.api1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.api1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.tracing.perfetto.handshake)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // DataStore
    implementation(libs.androidxDatastore)
    implementation(libs.androidxDatastorePreferencesCore)
    implementation(libs.androidxDatastorePreferencesRxJava3)
    implementation(libs.androidxDatastorePreferencesRxJava2)

    // Kotlin Coroutines
    implementation(libs.kotlinxCoroutinesAndroid)

    // Kotlin Serialization
    implementation(libs.kotlinxSerializationJson)

// for view model
    implementation(libs.lifecycleViewmodelKtx)
}