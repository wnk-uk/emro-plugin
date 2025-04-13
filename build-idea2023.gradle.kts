plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.emro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Apache Lucene
    implementation("org.apache.lucene:lucene-core:9.7.0")
    implementation("org.apache.lucene:lucene-analysis-common:9.7.0")
    implementation("org.apache.lucene:lucene-queryparser:9.7.0") // QueryParser 사용 시 필요
    implementation("org.apache.lucene:lucene-codecs:9.7.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.2")
    type.set("IU") // Target IDE Platform
    pluginName.set("emro-plugin")
    plugins.set(listOf("java"))

    instrumentCode.set(false) // 기본 instrumented jar 무효화 (선택)
    //downloadSources.set(false)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("999.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    prepareSandbox {
        dependsOn(shadowJar)
        from(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar")) {
            into("lib")
        }
    }

    runIde {
        jvmArgs(
            "-Xmx2048m",
            "-XX:ReservedCodeCacheSize=512m",
            "-XX:+UseG1GC",
            "-Didea.debug.mode=true",
            "-Djava.net.preferIPv4Stack=true",
            "-Dsun.awt.enableInputMethod=true",
            "-Djavafx.embed.singleThread=true"
        )
    }
}


