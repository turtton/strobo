import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("fabric-loom") version "0.10-SNAPSHOT"
    `maven-publish`
}

group = "dev.uten2c"
version = "47"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

sourceSets {
    val main = getByName("main")
    create("gametest") {
        compileClasspath += main.compileClasspath
        compileClasspath += main.output
        runtimeClasspath += main.runtimeClasspath
        runtimeClasspath += main.output
    }
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

repositories {
    mavenCentral()
}

fun DependencyHandlerScope.modIncludeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

dependencies {
    minecraft("com.mojang:minecraft:1.18.1")
    mappings("net.fabricmc:yarn:1.18.1+build.2:v2")
    modImplementation("net.fabricmc:fabric-loader:0.12.11")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.0+kotlin.1.6.0")
    modIncludeImplementation(fabricApi.module("fabric-api-base", "0.44.0+1.18"))
    modIncludeImplementation(fabricApi.module("fabric-resource-loader-v0", "0.44.0+1.18"))
    modIncludeImplementation(fabricApi.module("fabric-gametest-api-v1", "0.44.0+1.18"))
    modIncludeImplementation(fabricApi.module("fabric-registry-sync-v0", "0.44.0+1.18"))
}

loom {
    accessWidenerPath.set(file("src/main/resources/strobo.accesswidener"))
    runs {
        create("gameTest") {
            server()
            name("Game Test")
            runDir("build/gametest")
            source(sourceSets.getByName("gametest"))
        }
        create("autoGameTest") {
            server()
            name("Auto Game Test")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
            source(sourceSets.getByName("gametest"))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")
val remapSourcesJar = tasks.getByName<RemapSourcesJarTask>("remapSourcesJar")

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("${System.getProperty("user.home")}/private-repo")
        }
    }
}

tasks.withType<Test> {
    dependsOn(tasks.getByName("runAutoGameTest"))
}
