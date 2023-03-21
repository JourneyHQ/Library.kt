import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
}

group = "dev.yuua"
version = when (System.getenv("platform")) {
    "actions" -> System.getenv("project_version") ?: "${artifacts.hashCode()}_actions"
    else -> "${artifacts.hashCode()}_local"
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.tomacheese.com")
    maven("https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("net.dv8tion:JDA:5.0.0-beta.1")
    implementation("com.github.minndevelopment:jda-ktx:9fc90f6")

    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta5")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.reflections:reflections:0.10.2")
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
        if (System.getenv("platform") == "actions") {
            maven {
                name = "JourneyLib"
                url = uri("https://maven.pkg.github.com/yuuahp/JourneyLib")
                credentials {
                    username = System.getenv("project_user")
                    password = System.getenv("project_token")
                }
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            artifactId = "journeylib"
            from(components["java"])
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.6"
}
