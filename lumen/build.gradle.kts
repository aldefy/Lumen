plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.luminos"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)

    // Lifecycle for collectAsStateWithLifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Activity Compose for BackHandler
    implementation(libs.androidx.activity.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

val libraryVersion = "1.0.0-beta02"
val libraryGroup = "io.github.aldefy"
val libraryArtifact = "lumen"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = libraryGroup
            artifactId = libraryArtifact
            version = libraryVersion

            afterEvaluate {
                from(components["release"])
            }

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
    }

    repositories {
        maven {
            name = "sonatype"
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (libraryVersion.endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl

            // Read from: gradle.properties, local.properties, or environment variables
            val localProps = rootProject.file("local.properties").let { file ->
                if (file.exists()) {
                    java.util.Properties().apply { load(file.inputStream()) }
                } else null
            }

            credentials {
                username = findProperty("ossrhUsername") as String?
                    ?: localProps?.getProperty("ossrhUsername")
                    ?: System.getenv("OSSRH_USERNAME")
                    ?: ""
                password = findProperty("ossrhPassword") as String?
                    ?: localProps?.getProperty("ossrhPassword")
                    ?: System.getenv("OSSRH_PASSWORD")
                    ?: ""
            }
        }
    }
}

// Signing configuration - enable when GPG is configured
// For Central Portal, signing is required for release
// Configure GPG: https://central.sonatype.org/publish/requirements/gpg/
tasks.withType<Sign>().configureEach {
    onlyIf { project.hasProperty("signing.gnupg.keyName") }
}

signing {
    useGpgCmd()
    sign(publishing.publications["release"])
}

// Publish to local directory for Central Portal upload
// Run: ./gradlew :lumen:publishReleasePublicationToLocalRepository
// Then zip the output and upload to https://central.sonatype.com
tasks.register<Copy>("publishToLocalDir") {
    dependsOn("publishReleasePublicationToMavenLocal")
    from("${System.getProperty("user.home")}/.m2/repository/io/github/aldefy/lumen")
    into("${buildDir}/maven-central-upload")
}
