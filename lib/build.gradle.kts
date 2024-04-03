plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "net.slions.preference"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 29
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        multipleVariants {
            allVariants()
            withJavadocJar()
        }
    }
}


// Run task publishAllPublicationsToMavenRepository
// Zip up the content of \Preference\lib\build\maven\net\slions\android\preference\0.0.1
// Upload it to https://central.sonatype.com/publishing/deployments
// See: https://developer.android.com/build/publish-library/upload-library#create-pub
// See: https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "net.slions.android"
            artifactId = "preference"
            version = "0.0.1"

            pom {
                name = "Preference"
                description = "Android preference extensions"
                url = "https://github.com/Slion/Preference"
                /*properties = mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                )*/
                licenses {
                    license {
                        name = "GNU Lesser General Public License v3.0"
                        url = "https://github.com/Slion/Preference/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "Slion"
                        name = "Stéphane Lenclud"
                        email = "github@lenclud.com"
                    }
                }
                scm {
                    //connection = "scm:git:git://example.com/my-library.git"
                    //developerConnection = "scm:git:ssh://example.com/my-library.git"
                    url = "https://github.com/Slion/Preference"
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    // That gives us a task named publishAllPublicationsToMavenRepository
    repositories {
        maven {
            name = "maven"
            url = uri("${project.buildDir}/maven")
        }
    }
}

// Trying to zip it up but not working so far
tasks.register<Zip>("generateRepo") {
    //val publishTask = tasks.named(
       // "publishAllPublicationsToMavenRepository",
      //  PublishToMavenRepository::class.java)
    from(layout.buildDirectory.dir("mavem/net/slions/android/preference/0.0.1"))
    //from(publishTask.map { it.repository.url })
    into("preference")
    archiveFileName.set("preference.zip")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))
}


dependencies {
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}