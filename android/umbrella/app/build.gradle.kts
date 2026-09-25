import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

/**
 * Kunci OpenAI dibaca dari `local.properties` (tidak ikut ter-commit), bukan
 * di-hardcode. Salin `local.properties.example` lalu isi `LLM_API_KEY` — tanpa itu
 * `AIPosSDK.Builder().build()` melempar `IllegalArgumentException` saat dirakit,
 * karena umbrella ini selalu memuat agent AI.
 *
 * Kredensial MineSec dan kredensial registry GitHub Packages TIDAK lewat sini — lihat
 * `settings.gradle.kts`.
 */
val localProps = Properties().apply {
    val f = project.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.weekendinc.example.umbrella"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.weekendinc.example.umbrella"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "LLM_API_KEY", "\"${localProps.getProperty("LLM_API_KEY").orEmpty()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            // MineSec menarik Apache HttpComponents (httpclient + httpcore); keduanya
            // memaketkan file metadata dengan path yang sama, sehingga merger menolak.
            "META-INF/DEPENDENCIES",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/INDEX.LIST",
        )
    }
}

dependencies {
    implementation(libs.aipos.sdk)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)
}
