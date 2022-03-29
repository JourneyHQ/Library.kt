import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
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

    implementation("net.dv8tion:JDA:5.0.0-alpha.9")
    implementation("com.github.minndevelopment:jda-ktx:9f01b74")
    implementation("com.jagrosh:jda-utilities-commons:3.1.0")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("com.vdurmont:emoji-java:5.1.1")

    implementation("ch.qos.logback:logback-classic:1.3.0-alpha14")
    implementation("org.json:json:20211205")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("args4j:args4j:2.33")
    implementation("net.htmlparser.jericho:jericho-html:3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.reflections:reflections:0.10.2")

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.5")
    implementation("org.jraf:klibnotion:1.11.0")

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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.6"
}
