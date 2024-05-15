// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) version "8.2.0"  apply false
    alias(libs.plugins.jetbrains.kotlin.android) /*version "1.9.23"*/ apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
    }
}
