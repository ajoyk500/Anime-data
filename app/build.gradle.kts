plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

val dbSchemaLocation="$projectDir/schemas"
room {
    schemaDirectory(dbSchemaLocation)
}

android {
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    val packageName = "com.akcreation.gitsilent.play.pro"

    namespace = packageName
    compileSdk = 36

    defaultConfig {
        applicationId = packageName
        minSdk = 26
        targetSdk = 36
        versionCode = 121
        versionName = "1.1.4.5"

        buildConfigField("String", "FILE_PROVIDIER_AUTHORITY", """"$applicationId.file_provider"""")
        resValue("string", "file_provider_authority", "$applicationId.file_provider")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        ndk {
            // Sirf ek ABI rakhein - APK size 60-70% kam ho jayegi
            abiFilters += listOf("arm64-v8a")
        }

        // Resource configurations - sirf required languages
        resourceConfigurations += listOf("en", "hi")

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_STL=none")
                // Native code optimization
                cFlags += listOf("-Os", "-fvisibility=hidden", "-ffunction-sections", "-fdata-sections")
                cppFlags += listOf("-Os", "-fvisibility=hidden", "-ffunction-sections", "-fdata-sections")
            }
        }
    }

    // Signing Configuration
    signingConfigs {
        create("release") {
            // GitHub Actions ke liye environment variables check karein
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("KEY_ALIAS")
            val keyPassword = System.getenv("KEY_PASSWORD")

            if (keystoreFile != null && keystorePassword != null && keyAlias != null && keyPassword != null) {
                // GitHub Actions environment
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
                println("✓ Using keystore from GitHub Actions environment")
            } else {
                // Local build - direct values
                storeFile = file("../AK_CREATION_KEY.jks")  // Root directory mein hai
                storePassword = "ajoy70##"
                this.keyAlias = "ak_creation_key"
                this.keyPassword = "ajoy70##"
                println("✓ Using local keystore configuration")
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.31.6"
        }
    }

    buildTypes {
        release {
            // ProGuard/R8 enable karein - APK size bahut kam hogi
            isMinifyEnabled = true
            isShrinkResources = true
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "gson.pro"
            )
            
            signingConfig = signingConfigs.getByName("release")
            
            // Native libraries strip karein
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
        
        debug {
            // Debug mein size optimization disable - faster builds
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    
    // Split APKs by ABI - multiple smaller APKs banegi
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            isUniversalApk = false // Universal APK disable - smaller APKs
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        // Kotlin compiler optimizations
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
    
    kotlin {
        jvmToolchain(17)
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
        // Unnecessary features disable karein
        aidl = false
        renderScript = false
        shaders = false
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    
    packaging {
        resources {
            // Duplicate aur unnecessary files remove karein
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/ASL2.0")
            excludes.add("META-INF/*.kotlin_module")
            
            // Native debug symbols remove karein
            excludes.add("lib/*/libcrashlytics.so")
            excludes.add("lib/*/libc++_shared.so")
        }
        
        // Native libraries compress karein
        jniLibs {
            useLegacyPackaging = false
        }
        
        // Resources compress karein
        dex {
            useLegacyPackaging = false
        }
    }

    // Lint options - build time kam aur size optimize
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    ndkVersion = "28.2.13676358"
    
    // Bundle configuration for smaller size
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}

// Version code different ABIs ke liye
android.applicationVariants.all { variant ->
    variant.outputs.all { output ->
        val abiVersionCode = when {
            output.getFilter("ABI") == "arm64-v8a" -> 4
            output.getFilter("ABI") == "armeabi-v7a" -> 1
            output.getFilter("ABI") == "x86" -> 2
            output.getFilter("ABI") == "x86_64" -> 3
            else -> 0
        }
        output.versionCodeOverride = (variant.versionCode * 10) + abiVersionCode
    }
}

dependencies {
    // File encoding detector
    implementation("com.github.albfernandez:juniversalchardet:2.5.0")

    // Sora editor dependencies
    implementation("org.eclipse.jdt:org.eclipse.jdt.annotation:2.3.100")
    implementation("org.jruby.joni:joni:2.2.6")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("org.snakeyaml:snakeyaml-engine:2.10")

    // Markdown dependencies
    val markwonVersion = "4.6.2"
    val coilVersion = "2.7.0"
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:ext-strikethrough:$markwonVersion")
    implementation("io.noties.markwon:ext-tables:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")
    implementation("io.noties.markwon:ext-tasklist:$markwonVersion")
    implementation("com.github.jeziellago:Markwon:58aa5aba6a")

    // Coil for image loading
    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-gif:$coilVersion")
    implementation("io.coil-kt:coil-svg:$coilVersion")

    // Ktor for HTTP server
    val ktorVersion = "3.2.2"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    implementation("at.favre.lib:bcrypt:0.10.2")

    implementation("androidx.documentfile:documentfile:1.1.0")
    val lifecycleVersion = "2.9.2"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Room
    val room_version = "2.7.2"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // JSR305 for git24j
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.07.00")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Testing dependencies - release mein include nahi honge
    testImplementation(composeBom)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}