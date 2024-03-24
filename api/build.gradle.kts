import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "org.deg.uni"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")

    implementation(project(":algebra"))
    implementation(project(":analysis"))
    implementation(project(":graph"))
    implementation(project(":numeric"))
    implementation(project(":or"))
    implementation(project(":propa"))
    implementation(project(":utils"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
