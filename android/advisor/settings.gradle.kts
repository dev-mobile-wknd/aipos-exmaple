@file:Suppress("UnstableApiUsage")

/**
 * Contoh pemakaian domain **AI / LLM**: `aipos-advisor` (penyaran produk dari percakapan
 * sales–pelanggan). Modul ini murni Kotlin — tidak menyentuh MineSec, NFC, maupun
 * terminal pembayaran, jadi registry privat yang dibutuhkan cukup satu.
 *
 * Project ini berdiri sendiri — tidak ada source AI POS SDK di sini. Seluruh
 * `com.weekendinc.aipos:*` diambil dari GitHub Packages, persis seperti yang akan
 * dilakukan aplikasi merchant sungguhan.
 *
 * ## Kredensial
 *
 * Simpan di `~/.gradle/gradle.properties` (JANGAN di dalam proyek):
 *
 * ```properties
 * # AI POS SDK — personal access token GitHub dengan scope `read:packages`
 * GITHUB_PACKAGES_LOGIN=<username GitHub>
 * GITHUB_PACKAGES_TOKEN=<token>
 * ```
 *
 * Kunci LLM (dipanggil langsung, bukan lewat registry Maven) ada di
 * `local.properties.example` pada modul `app`.
 */
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val aiposLogin: String? = providers.gradleProperty("GITHUB_PACKAGES_LOGIN").orNull
val aiposToken: String? = providers.gradleProperty("GITHUB_PACKAGES_TOKEN").orNull
val aiposCredentialsComplete = !aiposLogin.isNullOrBlank() && !aiposToken.isNullOrBlank()

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            name = "AIPosSDK"
            url = uri("https://maven.pkg.github.com/dev-mobile-wknd/aipos-sdk-android")
            credentials {
                username = aiposLogin
                password = aiposToken
            }
            content { includeGroup("com.weekendinc.aipos") }
        }
    }
}

if (!aiposCredentialsComplete) {
    logger.warn(
        """
        |
        |  [aipos-example-advisor] GITHUB_PACKAGES_LOGIN / GITHUB_PACKAGES_TOKEN belum diisi.
        |  Dependency aipos-advisor akan gagal di-resolve (401). Isi di
        |  ~/.gradle/gradle.properties — lihat README.md.
        |
        """.trimMargin()
    )
}

rootProject.name = "aipos-example-advisor"
include(":app")
