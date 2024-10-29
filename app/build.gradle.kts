plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "statussaver.downloadstatus.imagevideodonwload.wadirectchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "statussaver.downloadstatus.imagevideodonwload.wadirectchat"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(libs.firebase.analytics)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.jsibbold:zoomage:1.3.1")
    implementation ("androidx.media3:media3-exoplayer:1.4.1")
    implementation ("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation ("androidx.media3:media3-ui:1.4.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("io.github.l4digital:fastscroll:2.0.1")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6" ) // or the latest version
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    implementation ("com.google.code.gson:gson:2.11.0")
}