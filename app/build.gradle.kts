plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.mycar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mycar"
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
        compose = true
        buildConfig = true
    }
}

dependencies {

    // ===========================
    // IMPLEMENTACIÓN APP
    // ===========================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.compose.ui:ui-text:1.7.3")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ===========================
    // TEST UNITARIOS (src/test)
    // ===========================

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")

    // Kotest (framework de tests para Kotlin)
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.kotest:kotest-extensions-junit5:5.9.1")

    // MockK (mocks en unit tests)
    testImplementation("io.mockk:mockk:1.13.10")

    // Corrutinas en tests (ViewModel, Repository, etc.)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // (Opcional) si tu libs.junit es JUnit4 y quieres dejarlo por compatibilidad:
    testImplementation(libs.junit)

    // ===========================
    // TESTS DE UI (src/androidTest)
    // ===========================
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Compose UI Test extra
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.3")
    androidTestImplementation("androidx.compose.ui:ui-test:1.7.3")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// ===========================
// HABILITA JUNIT5
// ===========================
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
