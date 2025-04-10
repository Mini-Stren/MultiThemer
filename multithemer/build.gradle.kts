import org.jetbrains.kotlin.konan.properties.loadProperties
import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jreleaser)
    id("maven-publish")
    id("signing")
}

kotlin {
    explicitApi()
}

android {
    namespace = "com.ministren.multithemer"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        androidResources = true
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xstring-concat=inline"
        )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
}

private val buildProperties = loadProperties("build.properties")
version = buildProperties.getProperty("VERSION_NAME")
description = buildProperties.getProperty("POM_DESCRIPTION")

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = buildProperties.getProperty("GROUP")
            artifactId = buildProperties.getProperty("POM_ARTIFACT_ID")

            pom {
                name.set(buildProperties.getProperty("POM_NAME"))
                description.set(buildProperties.getProperty("POM_DESCRIPTION"))
                url.set("https://github.com/Mini-Stren/MultiThemer")

                issueManagement {
                    url.set("https://github.com/Mini-Stren/MultiThemer/issues")
                }

                scm {
                    url.set("https://github.com/Mini-Stren/MultiThemer")
                    connection.set("scm:git://github.com/Mini-Stren/MultiThemer.git")
                    developerConnection.set("scm:git://github.com/Mini-Stren/MultiThemer.git")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("Mini-Stren")
                        name.set("Dmitry Koryakin")
                        email.set("mesonministren@gmail.com")
                    }
                }

                afterEvaluate {
                    from(components["release"])
                    withXml {
                        asNode()
                            .appendNode("build")
                            .appendNode("plugins")
                            .appendNode("plugin")
                            .apply {
                                appendNode("groupId", "com.simpligility.maven.plugins")
                                appendNode("artifactId", "android-maven-plugin")
                                appendNode("version", "4.6.0")
                                appendNode("extensions", "true")
                            }
                    }
                }
            }
        }
    }
    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    environment {
        variables.set(File("jreleaser.toml"))
    }
    project {
        inceptionYear = "2025"
        author("Mini-Stren")
    }
    gitRootSearch = true
    signing {
        active = Active.ALWAYS
        armored = true
        verify = true
    }
    release {
        github {
            skipRelease = true
            skipTag = true
        }
    }
    deploy {
        maven {
            mavenCentral.create("sonatype") {
                active = Active.ALWAYS
                url = "https://central.sonatype.com/api/v1/publisher"
                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
                setAuthorization("Basic")
                sign = true
                checksums = true
                sourceJar = true
                javadocJar = true
                retryDelay = 60
            }
        }
    }
}
