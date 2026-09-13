plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// ---------------------------------------------------------------------------------
// Release signing configuration.,
//
// GitHub Actions supplies the keystore + credentials as repository secrets and
// exports them as environment variables before running `assembleRelease` (see
// .github/workflows/build-release.yml). For local development, if these
// environment variables are not present, release builds automatically fall back
// to the debug keystore so `./gradlew assembleRelease` still works on your
// machine — that local APK just won't be suitable for the public update channel.
// ---------------------------------------------------------------------------------
val keystorePath: String? = System.getenv("KEYSTORE_FILE")
val keystorePassword: String? = System.getenv("KEYSTORE_PASSWORD")
val keyAliasEnv: String? = System.getenv("KEY_ALIAS")
val keyPasswordEnv: String? = System.getenv("KEY_PASSWORD")
val hasReleaseSigning = !keystorePath.isNullOrBlank() &&
    !keystorePassword.isNullOrBlank() &&
    !keyAliasEnv.isNullOrBlank() &&
    !keyPasswordEnv.isNullOrBlank() &&
    file(keystorePath).exists()

android {
    namespace = "com.osama.weather"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.osama.weather"
        minSdk = 26
        targetSdk = 36

        // ---------------------------------------------------------------------
        // versionCode / versionName — THE two numbers the update system compares.
        // Bump versionCode by exactly +1 on every release you push. See the
        // project README for the full release checklist.
        // ---------------------------------------------------------------------
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(keystorePath!!)
                storePassword = keystorePassword
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
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
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/*.version"
        }
    }
}

// Safety net: force these exact versions across the whole dependency graph,
// in case any transitive dependency (e.g. from play-services-location or
// datastore) requests a newer androidx.core/lifecycle/activity release than
// the ones this build is pinned to. Those newer releases raise the minimum
// required compileSdk to 37 (AGP 9.2+), which this project intentionally
// does not use — see the note in the dependencies block below.
configurations.all {
    resolutionStrategy {
        force(
            "androidx.core:core-ktx:1.18.0",
            "androidx.core:core:1.18.0",
            "androidx.lifecycle:lifecycle-runtime-ktx:2.9.2",
            "androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2",
            "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2",
            "androidx.lifecycle:lifecycle-runtime-compose:2.9.2",
            "androidx.lifecycle:lifecycle-process:2.9.2",
            "androidx.activity:activity-compose:1.11.0",
            "androidx.activity:activity-ktx:1.11.0",
            "androidx.activity:activity:1.11.0"
        )
    }
}

dependencies {
    // --- Core / Lifecycle ---
    // NOTE ON VERSIONS: androidx.core 1.19.0 and androidx.lifecycle 2.11.0+
    // raised their minimum required compileSdk to 37, which in turn requires
    // Android Gradle Plugin 9.2+. AGP 9.x switches to a new DSL that is
    // incompatible with the separate org.jetbrains.kotlin.android plugin used
    // here, so rather than migrating the whole build to that new toolchain,
    // these libraries are deliberately pinned just before that jump — fully
    // featured, still current as of mid-2026, and verified compatible with
    // AGP 8.13.2 / compileSdk 36.
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-process:2.9.2")
    implementation("androidx.activity:activity-compose:1.11.0")

    // --- Jetpack Compose (versions governed by the BOM) ---
    // Compose 1.12.0 (shipped in the 2026.08.xx BOM line) is the release that
    // adopted compileSdk 37 — see comment above. 2026.06.00 is the newest BOM
    // that still resolves to the pre-1.12.0 Compose stack.
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- Navigation ---
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // --- Networking: OkHttp for transport, kotlinx.serialization for JSON ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // --- Location & Play Services ---
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // --- Local storage (units, favorites, recent searches, cached last location) ---
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
