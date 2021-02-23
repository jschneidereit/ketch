plugins {
    id("ketch.kotlin-plugin-conventions")
    id("com.google.cloud.tools.jib") version "2.7.1"
    id("com.gradle.plugin-publish") version "0.12.0"
}

val jibVersion: String by project

dependencies {
    implementation("com.google.cloud.tools.jib:com.google.cloud.tools.jib.gradle.plugin:$jibVersion")
}
