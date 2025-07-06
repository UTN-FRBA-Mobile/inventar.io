import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

    // Code formatting plugin
    id("com.diffplug.spotless") version "7.0.4"
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_backing-property-naming" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
                "indent_size" to "4",
                "continuation_indent_size" to "4",
                "max_line_length" to "100",
            ),
        )
    }

    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
                "indent_size" to "4",
                "continuation_indent_size" to "4",
            ),
        )
    }
}

android {
    namespace = "ar.edu.utn.frba.inventario"
    compileSdk = 35

    defaultConfig {
        applicationId = "ar.edu.utn.frba.inventario"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API_BASE_URL from local.properties
        val localPropertiesFile = rootProject.file("local.properties")
        val apiBaseUrl = if (localPropertiesFile.exists()) {
            val properties = Properties()
            properties.load(FileInputStream(localPropertiesFile))
            properties.getProperty("API_BASE_URL", "http://default-url")
        } else {
            "http://default-url"
        }

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        compose = true
        buildConfig = true
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // For using the camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // For getting user location
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.kotlinx.coroutines.play.services)

    // For reading barcode
    implementation(libs.barcode.scanning)

    // For displaying images from URL
    implementation(libs.coil.compose)

    // For displaying GIFs
    implementation(libs.coil3.compose)
    implementation(libs.coil3.gif)
}
