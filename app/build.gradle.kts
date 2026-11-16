import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Lee las claves guardadas en local.properties
val localProperties = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) {
        archivo.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.example.proyectolince"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyectolince"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OPENWEATHER_API_KEY",
            "\"${localProperties.getProperty("OPENWEATHER_API_KEY", "")}\""
        )

        buildConfigField(
            "String",
            "LINCE_API_KEY",
            "\"${localProperties.getProperty("LINCE_API_KEY", "")}\""
        )

        buildConfigField(
            "String",
            "LINCE_BASE_URL",
            localProperties.getProperty("LINCE_BASE_URL")?:"\"https://ws.southbound.travel/lince-api/\""
        )

        val insforgeUrl = localProperties.getProperty("INSFORGE_URL") ?: ""
        val insforgeAnonKey = localProperties.getProperty("INSFORGE_ANON_KEY") ?: ""
        buildConfigField("String", "INSFORGE_URL", "\"$insforgeUrl\"")
        buildConfigField("String", "INSFORGE_ANON_KEY", "\"$insforgeAnonKey\"")
    }

    signingConfigs {
        create("release") {
            val keystorePath = localProperties.getProperty("KEYSTORE_FILE")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = rootProject.file(keystorePath)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD", "")
                keyAlias = localProperties.getProperty("KEY_ALIAS", "")
                keyPassword = localProperties.getProperty("KEY_PASSWORD", "")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            if (localProperties.getProperty("KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("release")
            }

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

    // Permite que JUnit pueda correr pruebas sobre clases Android sin fallar
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}



configurations.all {
    resolutionStrategy {
        force("androidx.test.espresso:espresso-core:3.6.1")
        force("androidx.test:runner:1.6.2")
        force("androidx.test:rules:1.6.1")
        force("androidx.test.ext:junit:1.2.1")
    }
}


dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("dev.insforge:insforge-kotlin:0.1.7")

    // ViewModel y Navigation
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.material:material-icons-extended")

    // Retrofit, OkHttp y Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coil para imágenes en Compose
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore para guardar token JWT, rol, recursoId e idioma ES/EN
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // WorkManager para la sincronización offline al recuperar el internet
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Geolocalización GPS en tiempo real
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Testing unitario con JUnit + MockK
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // Testing UI automatizado (Compose Test + Espresso compatible con Android 14/15)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")
    debugImplementation(libs.androidx.compose.ui.tooling)
}