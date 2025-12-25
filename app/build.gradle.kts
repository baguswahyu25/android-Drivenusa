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
        buildConfig = true
    }



    compileOptions {

        sourceCompatibility =  JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }

    configurations.all {
        resolutionStrategy {
            force ("androidx.core:core-ktx:1.12.0")
            force ("androidx.appcompat:appcompat:1.6.1")
        }
    }


    kotlin {

        compilerOptions {

            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)

        }

    }

    val navVersion = "2.7.3"
    dependencies {
        implementation("com.midtrans:uikit:2.3.0-SANDBOX")

        // Glide
        implementation("com.github.bumptech.glide:glide:4.16.0")
        kapt("com.github.bumptech.glide:compiler:4.16.0")
        implementation("androidx.navigation:navigation-fragment-ktx:${navVersion}")
        implementation("androidx.navigation:navigation-ui-ktx:${navVersion}")


        //datastore
        implementation("androidx.datastore:datastore-preferences:1.1.1")



//shimmer
        implementation("com.facebook.shimmer:shimmer:0.5.0")
// ✅ AndroidX Core & UI
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("com.google.android.material:material:1.11.0")



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

// ✅ Testing

        testImplementation("junit:junit:4.13.2")

        androidTestImplementation("androidx.test.ext:junit:1.1.5")

        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        implementation("com.airbnb.android:lottie:6.3.0")

        implementation("com.squareup.retrofit2:retrofit:2.11.0")

        implementation("com.squareup.retrofit2:converter-gson:2.11.0")

        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        implementation("com.google.android.gms:play-services-auth:20.7.0")

        implementation("de.hdodenhof:circleimageview:3.1.0")

    }



}