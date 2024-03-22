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
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:3.2.0")
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:2.3.0")
    implementation("org.slf4j:slf4j-nop:2.0.0-alpha7")
    implementation("org.jetbrains.lets-plot:lets-plot-batik:2.3.0")

    implementation(project(":propa"))
    implementation(project(":graph"))
    implementation(project(":utils"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}