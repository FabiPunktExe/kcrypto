plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.multiplatformLibrary) apply false
}

group = "de.fabiexe.kcrypto"
version = "1.0.0"

subprojects {
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    configure<PublishingExtension> {
        repositories {
            maven("https://repo.diruptio.de/repository/maven-public-releases") {
                name = "DiruptioPublic"
                credentials {
                    username = (System.getenv("DIRUPTIO_REPO_USERNAME") ?: project.findProperty("maven_username") ?: "").toString()
                    password = (System.getenv("DIRUPTIO_REPO_PASSWORD") ?: project.findProperty("maven_password") ?: "").toString()
                }
            }
        }
    }
}