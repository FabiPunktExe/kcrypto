import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatformLibrary)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvmToolchain(21)
    jvm()

    android {
        namespace = "de.fabiexe.kcrypto.aes"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    js {
        browser()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        webMain.dependencies {
            implementation(libs.kotlin.wrapper.browser)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}