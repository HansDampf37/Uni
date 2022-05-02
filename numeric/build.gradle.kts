plugins {
    kotlin("jvm")
}

group = "org.deg.uni"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(project(":algebra"))
    implementation(project(":analysis"))
    implementation(project(":graph"))
    implementation(project(":utils"))
    implementation(project(":propa"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}