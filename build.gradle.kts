import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.emro"
version = "1.1"


repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
//
dependencies {
    // Apache Lucene
    implementation("org.apache.lucene:lucene-core:9.12.0")
    implementation("org.apache.lucene:lucene-analysis-common:9.12.0")
    implementation("org.apache.lucene:lucene-queryparser:9.12.0") // QueryParser 사용 시 필요
    implementation("org.apache.lucene:lucene-codecs:9.12.0")
    intellijPlatform {
        intellijIdeaUltimate("2024.3.5")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform  {
    pluginConfiguration {
        name = "Caidentia-plugin";
    }
    //version.set("2024.3.5")
    //type.set("IU") // Target IDE Platform
    //pluginName.set("emro-plugin")
    //plugins.set(listOf("java"))
    //plugins {
//        listOf("java")
    //}
    instrumentCode.set(false) // 기본 instrumented jar 무효화 (선택)
    //downloadSources.set(false)
    buildSearchableOptions.set(false)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
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


