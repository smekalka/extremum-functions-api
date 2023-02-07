import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    `maven-publish`

}

val artifactVersion = "0.0.12"
val artifact = "functions-api"
val extremumGroup = "io.extremum"
val releasesRepoUrl = "https://artifactory.extremum.monster/artifactory/extremum-releases/"
val snapshotsRepoUrl = "https://artifactory.extremum.monster/artifactory/extremum-snapshots/"
val extremumToolsVersion = "0.0.4"

group = extremumGroup
version = artifactVersion
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri(snapshotsRepoUrl)
        credentials {
            username = System.getenv("ARTIFACTORY_USER")
            password = System.getenv("ARTIFACTORY_PASSWORD")
        }
        mavenContent {
            snapshotsOnly()
        }
    }
    maven {
        url = uri(releasesRepoUrl)
        credentials {
            username = System.getenv("ARTIFACTORY_USER")
            password = System.getenv("ARTIFACTORY_PASSWORD")
        }
    }
}

dependencies {
    implementation("io.extremum:extremum-shared-models:2.1.17-SNAPSHOT")
    implementation("io.extremum.functions:xdoc-java:1.0.6")
    implementation("io.extremum:extremum-model-tools:$extremumToolsVersion")
    testImplementation("io.extremum:extremum-test-tools:$extremumToolsVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.6.4")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = extremumGroup
            artifactId = artifact
            version = artifactVersion

            from(components["java"])
        }

        repositories {
            maven {
                val isReleaseVersion = !(version as String).endsWith("-SNAPSHOT")
                url = uri(if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl)
                credentials {
                    username = System.getenv("ARTIFACTORY_USER")
                    password = System.getenv("ARTIFACTORY_PASSWORD")
                }
            }
        }
    }
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}
