import com.android.build.gradle.internal.packaging.defaultExcludes

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.todolist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.todolist"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes+=("com/google/protobuf/*.proto")
            excludes+=("com/google/protobuf/**")
            excludes+=("com/google/protobuf/")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("com.google.firebase:firebase-bom:32.0.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.protobuf:protobuf-java:3.21.7")
    implementation("com.google.firebase:firebase-firestore:24.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.protobuf:protobuf-javalite:3.21.7") {
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
    }
    implementation("com.google.protobuf:protobuf-java:3.21.7") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-java:3.21.7")
        //force("com.google.protobuf:protobuf-javalite:3.21.7")
    }
}
