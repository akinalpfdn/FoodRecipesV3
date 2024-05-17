plugins {
    alias(libs.plugins.android.application) version "8.2.0"
    alias(libs.plugins.jetbrains.kotlin.android) /*version "1.9.23" */
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodrecipesv3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodrecipesv3"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        renderscriptTargetApi = 18
        renderscriptSupportModeEnabled = true

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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //implementation(libs.gradle)
    implementation(libs.google.services)
    implementation(libs.material) // Specify version
    implementation(libs.androidx.cardview)           // Specify version
    implementation(libs.androidx.recyclerview)   // Specify version


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}