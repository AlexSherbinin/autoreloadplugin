plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
}

publishing {

}

group = "com.github.AlexSherbinin"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}