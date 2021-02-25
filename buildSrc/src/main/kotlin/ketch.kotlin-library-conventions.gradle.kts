plugins {
    id("ketch.kotlin-common-conventions")
    `java-library`
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes",  "-Xopt-in=kotlin.RequiresOptIn")
}