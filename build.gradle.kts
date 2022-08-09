//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
//    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ir.smmh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
//    implementation(kotlin("stdlib-jdk8"))
//    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:+")
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
//        freeCompilerArgs += "-include-runtime"
    }
}

//tasks.withType<ShadowJar> {
//
//    manifest {
////        put("Implementation-Title", "Gradle Jar File Example")
////        put("Implementation-Version" version)
//        attributes["Main-Class"] = "MainKt"
//    }
//}



//tasks.withType<Jar> {
//    manifest {
//        attributes["Main-Class"] = "MainKt"
////        println(configurations.map { it.name })
//    }




//    configurations.forEach { if (it.isCanBeResolved) dependsOn(it) }

//    val allDependencies: MutableSet<File> = HashSet()
//
//    configurations.forEach {
//        if (it.isCanBeResolved && it.isVisible) {
////            println()
////            println(it.name)
////            println("=".repeat(it.name.length))
////            println(it.joinToString("\n") {
////                it.name
////            })
//            it.forEach {
//                allDependencies.add(it)
//            }
//
//        }
//    }
//
//    println(allDependencies.joinToString("\n") { it.name })
//
//    from({
//        allDependencies.map {
//            if (it.isDirectory()) zipTree(it) else it
//        }
//    })

//    // To add all of the dependencies
//    from(sourceSets.main.get().output)
//    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
//    from({
//        configurations.compileClasspath.get().map {
//            if (it.isDirectory()) zipTree(it) else it
//        }
//    })
//
//}
