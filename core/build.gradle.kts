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
        namespace = "de.fabiexe.kcrypto.core"
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

    mingwX64 {
        binaries {
            sharedLib()
            staticLib()
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        nativeMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}