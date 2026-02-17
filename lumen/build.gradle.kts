import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.roborazzi)
    id("maven-publish")
    id("signing")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "lumen"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.kotlinx.atomicfu)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }

        val jvmTest by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.roborazzi)
                implementation(libs.roborazzi.compose)
                implementation(libs.robolectric)
                implementation(libs.androidx.compose.ui.test.junit4)
                implementation(libs.androidx.compose.ui.test.manifest)
                implementation(libs.androidx.test.core)
            }
        }
    }
}

android {
    namespace = "io.luminos"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

val libraryVersion = "1.0.0-beta06"
val libraryGroup = "io.github.aldefy"
val libraryArtifact = "lumen"

group = libraryGroup
version = libraryVersion

publishing {
    publications.withType<MavenPublication>().configureEach {
        val publicationName = name
        val javadocJar = tasks.register("${publicationName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(publicationName)
        }
        artifact(javadocJar)
        pom {
            name.set("Lumen")
            description.set("Compose Multiplatform coachmark library with transparent cutouts, 6 animations, and customizable tooltips.")
            url.set("https://github.com/aldefy/Lumen")
            inceptionYear.set("2024")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("aldefy")
                    name.set("Adit Lal")
                    email.set("aditlal@gmail.com")
                    url.set("https://github.com/aldefy")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/aldefy/Lumen.git")
                developerConnection.set("scm:git:ssh://github.com/aldefy/Lumen.git")
                url.set("https://github.com/aldefy/Lumen")
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/aldefy/Lumen/issues")
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (libraryVersion.endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl

            credentials {
                username = findProperty("ossrhUsername")?.toString()
                    ?: System.getenv("OSSRH_USERNAME")
                    ?: ""
                password = findProperty("ossrhPassword")?.toString()
                    ?: System.getenv("OSSRH_PASSWORD")
                    ?: ""
            }
        }
        maven {
            name = "localStaging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf { project.hasProperty("signing.gnupg.keyName") }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
