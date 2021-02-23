plugins {
    kotlin("jvm")
}

repositories {
    jcenter()
    mavenCentral()
    maven(url="https://kotlin.bintray.com/kotlinx")
}

val kotestVersion: String by project

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.kotest:kotest-framework-engine-jvm:$kotestVersion")
}

tasks.test {
    // Use junit platform for unit tests.
    // useJUnitPlatform()
}
