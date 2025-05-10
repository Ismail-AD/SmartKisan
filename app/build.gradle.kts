import com.android.build.api.dsl.AaptOptions
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp")
}

android {

    namespace = "com.appdev.smartkisan"
    compileSdk = 34

    val localProperties = Properties()
    localProperties.load(File(rootDir, "local.properties").inputStream())

    val supabaseKey: String = localProperties.getProperty("supabaseKey") ?: ""
    val supabaseUrl: String = localProperties.getProperty("supabaseUrl") ?: ""
    val geminiKey: String = localProperties.getProperty("geminikey") ?: ""
    val weatherKey: String = localProperties.getProperty("weatherkey") ?: ""
    val newsKey: String = localProperties.getProperty("newskey") ?: ""

    defaultConfig {
        applicationId = "com.appdev.smartkisan"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "GEMINI_KEY", "\"$geminiKey\"")
        buildConfigField("String", "WEATHER_KEY", "\"$weatherKey\"")
        buildConfigField("String", "NEWS_KEY", "\"$newsKey\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
        mlModelBinding = true
    }
    aaptOptions {
        noCompress
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


}

dependencies {


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("dev.shreyaspatil.generativeai:generativeai-google:0.9.0-1.1.0")
    implementation("com.google.firebase:firebase-firestore:25.1.2")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.google.maps.android:maps-compose:4.3.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.11.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("dev.chrisbanes.haze:haze-jetpack-compose:0.4.1")

    implementation("io.coil-kt.coil3:coil-compose:3.0.3")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.3")
    //pager
    implementation("com.google.accompanist:accompanist-pager:0.24.13-rc")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("com.github.tfaki:ComposableSweetToast:1.0.1")

    implementation("com.airbnb.android:lottie-compose:6.1.0")
    // hilt
    kapt("com.google.dagger:hilt-android-compiler:2.55")
    implementation("com.google.dagger:hilt-android:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.1.1")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.1.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:3.1.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.1.1")
    implementation("io.ktor:ktor-client-android:3.1.1")
    implementation("io.ktor:ktor-client-okhttp:3.1.1")
    implementation("io.ktor:ktor-client-plugins:3.1.1")
    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

}