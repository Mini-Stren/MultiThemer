import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ministren.demoapp.multithemer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ministren.demoapp.multithemer"
        minSdk = 21
        targetSdk = 35

        val buildProperties = loadProperties("build.properties")
        versionCode = buildProperties.getProperty("VERSION_CODE").toInt()
        versionName = buildProperties.getProperty("VERSION_NAME")
    }

    buildFeatures {
        viewBinding = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = getValueFromEnvOrLocalProperties("SIGNING_STORE_PASSWORD")
            keyAlias = getValueFromEnvOrLocalProperties("SIGNING_KEY_ALIAS")
            keyPassword = getValueFromEnvOrLocalProperties("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(project(":multithemer"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.material)
    implementation(libs.androidbroadcast.vbpd)
}

private fun getValueFromEnvOrLocalProperties(
    envKeyName: String,
    localPropertiesKeyName: String = envKeyName,
): String? {
    return System.getenv(envKeyName)
        ?: gradleLocalProperties(rootDir, providers).getProperty(localPropertiesKeyName)
}
