import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)   // Kotlin 2.0+ bundled Compose compiler
    kotlin("plugin.serialization") version "2.0.21"
    kotlin("kapt")
}

// read sb url and api key from local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it)}
}
val supabaseUrl =  localProperties["SUPABASE_URL"] as String? ?: ""
val supabaseKey =  localProperties["SUPABASE_ANON_KEY"] as String? ?:""

android {
    namespace = "com.example.madproject"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
        compose = true   // enables Compose UI toolkit
    }

    defaultConfig {
        applicationId = "com.example.madproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SUPABASE_URL", "\"${supabaseUrl}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${supabaseKey}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.0.0")

    // viewmodel survives rotation, lifecyclescope does not
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // room is the local sqlite db with type-safe kotlin api
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")       // adds coroutine + flow support
    kapt("androidx.room:room-compiler:2.6.1")            // generates the db implementation code

    // --- Jetpack Compose ---
    // BOM pins all compose library versions so they're always compatible with each other
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // activity-compose provides setContent {} and other Activity<->Compose bridges
    implementation("androidx.activity:activity-compose:1.9.3")
    // navigation-compose provides NavHost and NavController
    implementation("androidx.navigation:navigation-compose:2.8.5")
    // viewModel() composable — creates/retrieves VM scoped to nav backstack entry
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // WorkManager — background stats sync (was used but missing from deps)
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    debugImplementation("androidx.compose.ui:ui-tooling") // layout inspector support

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}