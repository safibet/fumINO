// Percorso alternativo per chi ha Android Studio.
// Nota: prima di usarlo, togli l'attributo package="com.fumino.app" da
// app/src/main/AndroidManifest.xml (serve solo alla build senza Gradle:
// con AGP 8 il namespace si dichiara qui sotto).
plugins {
    id("com.android.application") version "8.5.2"
}

android {
    namespace = "com.fumino.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fumino.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
