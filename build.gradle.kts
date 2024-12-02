plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.emro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Apache Lucene
    implementation("org.apache.lucene:lucene-core:9.8.0")
    implementation("org.apache.lucene:lucene-analysis-common:9.8.0")
    implementation("org.apache.lucene:lucene-queryparser:9.8.0") // QueryParser 사용 시 필요
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.8")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("java"))
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
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    runIde {
        jvmArgs(
            "-Xmx2048m",
            "-XX:ReservedCodeCacheSize=512m",
            "-XX:+UseG1GC",
            "-Didea.debug.mode=true",
            "-Djava.net.preferIPv4Stack=true"
        )
    }

    runIdeForUiTests {
        jvmArgs(
            "-Xmx2048m",
            "-XX:ReservedCodeCacheSize=512m",
            "-XX:+UseG1GC",
            "-Didea.debug.mode=true",
            "-Djava.net.preferIPv4Stack=true"
        )
    }
}
