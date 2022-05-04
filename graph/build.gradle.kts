plugins {
    kotlin("jvm")
}

group = "org.deg.uni"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation(project(":utils"))
    implementation("org.graphstream:gs-core:2.0")
    implementation("org.graphstream:gs-ui-swing:2.0")
    implementation("org.graphstream:gs-ui:1.3")
    implementation("org.graphstream:gs-algo:2.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}