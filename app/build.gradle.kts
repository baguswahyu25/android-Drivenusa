plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
}



android {

    namespace = "projectichif.DriveNusa"

    compileSdk = 36



    defaultConfig {

        applicationId = "projectichif.DriveNusa"

        minSdk = 24

        targetSdk = 34

        versionCode = 1

        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }



    buildFeatures {

        viewBinding = true

    }



    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17

        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true

    }



    kotlin {

        compilerOptions {

            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)

        }

    }

    configurations.all {

        resolutionStrategy {

            force("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

            force("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

            force("androidx.activity:activity-ktx:1.9.3")

            force("androidx.core:core-ktx:1.12.0")

        }

    }

    val navVersion = "2.7.3"

    dependencies {
        // Glide
        implementation("com.github.bumptech.glide:glide:4.16.0")
        kapt("com.github.bumptech.glide:compiler:4.16.0")
        implementation("androidx.navigation:navigation-fragment-ktx:${navVersion}")
        implementation("androidx.navigation:navigation-ui-ktx:${navVersion}")
        implementation ("com.github.bumptech.glide:glide:4.16.0")
        // ===== FIREBASE (FCM) =====
        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
        implementation("com.google.firebase:firebase-messaging")
        implementation("androidx.datastore:datastore-preferences:1.1.1")
        implementation("com.android.volley:volley:1.2.1")

//shimmer
        implementation("com.facebook.shimmer:shimmer:0.5.0")
// ✅ AndroidX Core & UI

        implementation("androidx.core:core-ktx:1.12.0")

        implementation("androidx.appcompat:appcompat:1.7.0")

        implementation("com.google.android.material:material:1.12.0")



// ✅ Lifecycle & Activity (sinkron)

        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

        implementation("androidx.activity:activity-compose:1.9.3")





// ✅ Ktor 3.0.0 kompatibel Supabase 3.2.5

        implementation("io.ktor:ktor-client-core:3.0.0")

        implementation("io.ktor:ktor-client-cio:3.0.0")

        implementation("io.ktor:ktor-client-android:3.0.0")

        implementation("io.ktor:ktor-client-content-negotiation:3.0.0")

        implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

        implementation("io.ktor:ktor-client-logging:3.0.0")



// ✅ Coroutines

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")



// ✅ Desugar agar Java 17 aman di Android

        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")



// ✅ Testing

        implementation("com.google.firebase:firebase-messaging:23.4.1")
        testImplementation("junit:junit:4.13.2")

        androidTestImplementation("androidx.test.ext:junit:1.1.5")

        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        implementation("com.airbnb.android:lottie:6.3.0")

        implementation("com.squareup.retrofit2:retrofit:2.11.0")

        implementation("com.squareup.retrofit2:converter-gson:2.11.0")

        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        implementation("com.google.android.gms:play-services-auth:20.7.0")

        implementation("de.hdodenhof:circleimageview:3.1.0")

        implementation("com.airbnb.android:lottie:6.3.0")
        implementation("com.google.android.material:material:1.12.0")

    }



}

dependencies {

    implementation(libs.androidx.appcompat)

    implementation(libs.material)

    implementation(libs.androidx.activity)

    implementation(libs.androidx.constraintlayout)

}