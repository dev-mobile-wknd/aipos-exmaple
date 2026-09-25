@file:Suppress("UnstableApiUsage")

/**
 * Contoh pemakaian domain **pembayaran**: `aipos-payment` (tap kartu di terminal) dan
 * `aipos-payment-online` (tautan Virtual Account / QRIS via WebView).
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
 *
 * # MineSec Headless SDK — diminta ke MineSec customer support.
 * # Wajib: aipos-payment menarik com.theminesec.sdk:headless-stage saat runtime,
 * # jadi build APK tetap butuh registry ini walau kartu belum pernah ditap.
 * MINESEC_REGISTRY_LOGIN=<login>
 * MINESEC_REGISTRY_TOKEN=<token>
 * ```
 */
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

data class RegistryCredentials(val login: String?, val token: String?) {
    val isComplete: Boolean get() = !login.isNullOrBlank() && !token.isNullOrBlank()
}

fun registryCredentials(loginKey: String, tokenKey: String) = RegistryCredentials(
    login = providers.gradleProperty(loginKey).orNull,
    token = providers.gradleProperty(tokenKey).orNull,
)

val aiposCredentials = registryCredentials("GITHUB_PACKAGES_LOGIN", "GITHUB_PACKAGES_TOKEN")
val minesecCredentials = registryCredentials("MINESEC_REGISTRY_LOGIN", "MINESEC_REGISTRY_TOKEN")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            name = "AIPosSDK"
            url = uri("https://maven.pkg.github.com/dev-mobile-wknd/aipos-sdk-android")
            credentials {
                username = aiposCredentials.login
                password = aiposCredentials.token
            }
            content { includeGroup("com.weekendinc.aipos") }
        }

        maven {
            name = "MineSecMavenClientRegistry"
            url = uri("https://maven.pkg.github.com/theminesec/ms-registry-client")
            credentials {
                username = minesecCredentials.login
                password = minesecCredentials.token
            }
            content { includeGroupByRegex("com\\.theminesec.*") }
        }
    }
}

val missingCredentials = buildList {
    if (!aiposCredentials.isComplete) add("GITHUB_PACKAGES_LOGIN / GITHUB_PACKAGES_TOKEN (AI POS SDK)")
    if (!minesecCredentials.isComplete) add("MINESEC_REGISTRY_LOGIN / MINESEC_REGISTRY_TOKEN (MineSec)")
}
if (missingCredentials.isNotEmpty()) {
    logger.warn(
        """
        |
        |  [aipos-example-payment] Kredensial registry belum diisi:
        |${missingCredentials.joinToString("\n") { "    - $it" }}
        |
        |  Dependency SDK akan gagal di-resolve (401). Isi di ~/.gradle/gradle.properties —
        |  lihat README.md.
        |
        """.trimMargin()
    )
}

rootProject.name = "aipos-example-payment"
include(":app")
