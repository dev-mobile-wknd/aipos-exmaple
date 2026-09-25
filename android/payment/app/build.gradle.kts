plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

/**
 * Contoh domain **pembayaran**: `aipos-payment` (tap kartu di terminal) dan
 * `aipos-payment-online` (Virtual Account / QRIS via WebView).
 *
 * Tidak ada rahasia yang perlu dibaca dari `local.properties` di sini — kedua modul
 * bisa didemokan dengan nilai bawaannya sendiri:
 * - `aipos-payment` tanpa kredensial MineSec di Gradle otomatis jatuh ke
 *   `SimulatorPaymentGateway` (tap selalu disetujui).
 * - `aipos-payment-online` tanpa `OnlinePaymentConfig.apiKey` memakai akun beta bersama
 *   milik SDK.
 */
android {
    namespace = "com.weekendinc.example.payment"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.weekendinc.example.payment"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
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
    implementation(libs.aipos.payment)
    implementation(libs.aipos.payment.online)

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
