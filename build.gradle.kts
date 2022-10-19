plugins {
    id("java")
    val kotlinVersion = "1.7.0"
    kotlin("jvm") version kotlinVersion

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    api("com.github.whiterasbk:mider:beta0.9.14")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}