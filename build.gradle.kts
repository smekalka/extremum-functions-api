import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.13"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    java
    `maven-publish`
    signing
}

val extremumGroup = "io.extremum"
val extremumVersion = "3.0.0"
val artifact = "functions-api"
val artifactDescription = "Api for functions' package"
val artifactUrl = "github.com/smekalka/extremum-functions-api"
val artifactVersion = "3.2.0-rc.8"
val extremumToolsVersion = "3.2.0-rc.4"

group = extremumGroup
version = artifactVersion
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.extremum:extremum-shared-models:$extremumVersion")
    implementation("io.extremum:xdoc-java:$extremumVersion")
    implementation("io.extremum:extremum-model-tools:$extremumToolsVersion")
    testImplementation("io.extremum:extremum-test-tools:$extremumToolsVersion")

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
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
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = extremumGroup
            artifactId = artifact
            version = artifactVersion

            from(components["java"])

            pom {
                name.set(artifact)
                description.set(artifactDescription)
                url.set("https://$artifactUrl")
                inceptionYear.set("2022")

                scm {
                    url.set("https://$artifactUrl")
                    connection.set("scm:https://$artifactUrl.git")
                    developerConnection.set("scm:git://$artifactUrl.git")
                }

                licenses {
                    license {
                        name.set("Business Source License 1.1")
                        url.set("https://$artifactUrl/blob/develop/LICENSE.md")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("SherbakovaMA")
                        name.set("Maria Sherbakova")
                        email.set("m.sherbakova@smekalka.com")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            val isReleaseVersion = !(version as String).endsWith("-SNAPSHOT")
            url = uri(if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl)
            credentials {
                username = System.getProperty("ossrhUsername")
                password = System.getProperty("ossrhPassword")
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}