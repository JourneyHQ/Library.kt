import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
}

group = "dev.yuua"
version = artifacts.hashCode()

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.tomacheese.com")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha10")
    implementation("net.dv8tion:JDA:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation("com.jagrosh:jda-utilities-commons:3.1.0")
    implementation("org.json:json:20211205")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("args4j:args4j:2.33")
    implementation("net.htmlparser.jericho:jericho-html:3.4")
    implementation("org.jraf:klibnotion:1.10.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

publishing {
    repositories {
        maven {
            name = "JourneyLib"
            url = uri("file:/${project.projectDir}/artifacts")
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            artifactId = "journeylib"
            from(components["java"])
        }
    }
}