plugins {
    kotlin("jvm") version "1.6.20"
}

group = "org.deg.uni"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(project(":analysis"))
    implementation(project(":graph"))
    implementation(project(":propa"))
    implementation(project(":utils"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}