plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
    id ("kotlin-kapt")
}

val libVersion = "0.1.0"

android {
    // Notably define R class namespace
    namespace = "slions.pref"
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

// Define our publishing tasks which will generate our upload archive folder layout and content
// See: https://developer.android.com/build/publish-library/upload-library#create-pub
// See: https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "net.slions.android"
            artifactId = "preference"
            version = libVersion

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
            url = uri(layout.buildDirectory.dir("maven"))
        }
    }
}

// Take care of signing our build artifacts.
// For each of our AAR, JAR, POM and MODULE files it will create a corresponding ASC file.
// For this to work you should setup your user level gradle.properties as explained there:
// https://docs.gradle.org/7.4.2/userguide/signing_plugin.html#sec:using_gpg_agent
// Should just specify key ID and password like that:
// signing.gnupg.keyName=<key-id>
// signing.gnupg.passphrase=<key-password>
signing {
    // Use installed GPG rather than built-in outdated version
    useGpgCmd()
    // Sign all publications I guess
    sign(publishing.publications)
    //sign(publishing.publications["release"])
}

// Define a task to generate the ZIP we can upload to Maven Central
// It will create a file named preference.zip inside \build\distributions folder
// You can then upload it to https://central.sonatype.com/publishing/deployments for publishing
tasks.register<Zip>("generateUploadPackage") {
    // Take the output of our publishing
    val publishTask = tasks.named(
        "publishReleasePublicationToMavenRepository",
        PublishToMavenRepository::class.java)
    from(publishTask.map { it.repository.url })
    // Exclude maven-metadata.xml as Sonatype fails upload validation otherwise
    exclude {
        // Exclude left over directories not matching current version
        // That was needed otherwise older versions empty directories would be include in our ZIP
        if (it.file.isDirectory && it.path.matches(Regex(""".*\d+\.\d+.\d+$""")) && !it.path.contains(libVersion)) {
//            project.logger.lifecycle("Exclude version ${it.name}")
//            project.logger.lifecycle(it.path)
//            project.logger.lifecycle(it.name)
//            project.logger.lifecycle("--------")
            return@exclude true
        }

        // Only take files inside current version directory
        // Notably excludes maven-metadata.xml which Maven Central upload validation does not like
        (it.file.isFile && !it.path.contains(libVersion))
    }

    // Name our zip file
    archiveFileName.set("preference.zip")
}


dependencies {
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.material:material:1.11.0")
    // Logs
    implementation("com.jakewharton.timber:timber:5.0.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}