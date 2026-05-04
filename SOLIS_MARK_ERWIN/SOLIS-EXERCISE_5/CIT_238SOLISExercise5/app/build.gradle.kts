plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.exercise_2_music_player"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.exercise_2_music_player"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

// Fix for "Duplicate resources" error: 
// Automatically delete duplicate .webp launcher icons if .png versions exist during the resource merge task.
tasks.configureEach {
    if (name.contains("merge", ignoreCase = true) && name.contains("Resources", ignoreCase = true)) {
        doFirst {
            val resDir = file("src/main/res")
            if (resDir.exists()) {
                fileTree(resDir).matching {
                    include("**/ic_launcher.webp")
                    include("**/ic_launcher_round.webp")
                }.forEach { webpFile ->
                    // Check if a corresponding .png file exists in the same folder
                    val pngFile = webpFile.parentFile.resolve(webpFile.name.replace(".webp", ".png"))
                    if (pngFile.exists()) {
                        println("Deleting duplicate resource to fix build error: ${webpFile.absolutePath}")
                        webpFile.delete()
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    // ExoPlayer for streaming audio from URLs
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.media3:media3-common:1.1.1")
}
