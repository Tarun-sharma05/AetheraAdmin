
plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.3.20"

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.aetheraadmin"
    compileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)

    defaultConfig {
        applicationId = "com.example.aetheraadmin"
        minSdkVersion(rootProject.extra["defaultMinSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["defaultTargetSdkVersion"] as Int)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = "17"
//    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //FIREBASE
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.9.7")
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

//    //DI - HILT
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")

    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")



    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")


//    implementation("io.coil-kt.coil3:coil-compose:")
//    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc01")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Navigation 3
    implementation("androidx.navigation3:navigation3-ui:1.1.0")
    implementation("androidx.navigation3:navigation3-runtime:1.1.0")
    // Required for rememberViewModelStoreNavEntryDecorator (ViewModel scoping per nav entry)
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.9.0")
    implementation("androidx.compose.material3:material3:1.4.0")

}