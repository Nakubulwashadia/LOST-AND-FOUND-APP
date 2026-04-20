// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false // Use kotlin.android instead of kotlin.compose here
    alias(libs.plugins.google.services) apply false
}