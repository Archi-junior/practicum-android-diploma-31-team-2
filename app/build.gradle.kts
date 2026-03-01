plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("ru.practicum.android.diploma.plugins.developproperties")
    id("com.google.devtools.ksp") version "2.1.20-1.0.31"
    id("io.gitlab.arturbosch.detekt")
    id("kotlin-parcelize")
}

android {
    namespace = "ru.practicum.android.diploma"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.practicum.android.diploma"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(type = "String", name = "API_ACCESS_TOKEN", value = "\"${developProperties.apiAccessToken}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
        viewBinding = true
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false

        reports {
            html.enabled = true
            xml.enabled = true
            txt.enabled = true
            xml.outputLocation.set(file("build/reports/detekt/detekt.xml"))
        }
    }

    tasks.register("detektAll") {
        dependsOn("detekt")
        description = "Run detekt analysis for all modules"
        group = "verification"
    }
}

dependencies {
    implementation(libs.androidX.core)
    implementation(libs.androidX.appCompat)

    // UI layer libraries
    implementation(libs.ui.material)
    implementation(libs.ui.constraintLayout)

    // region Unit tests
    testImplementation(libs.unitTests.junit)
    // endregion

    // region UI tests
    androidTestImplementation(libs.uiTests.junitExt)
    androidTestImplementation(libs.uiTests.espressoCore)
    // endregion

    // Lifecycle (ViewModel, LiveData)
    implementation(libs.androidX.lifecycle.viewmodel)
    implementation(libs.androidX.lifecycle.livedata)

    // Navigation Component
    implementation(libs.androidX.navigation.fragment)
    implementation(libs.androidX.navigation.ui)

    // Koin DI
    implementation(libs.koin.android)

    // Room
    implementation(libs.androidX.room.runtime)
    implementation(libs.androidX.room.ktx)
    ksp(libs.androidX.room.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
}
