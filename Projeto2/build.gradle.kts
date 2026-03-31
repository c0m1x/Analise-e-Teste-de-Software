plugins {
    id("java")
    id("application")
    id("jacoco")
    id("info.solidsoft.pitest") version "1.15.0"
}

import org.gradle.api.tasks.SourceSetContainer
import java.io.File

group = "org.spotifumtp37"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val sourceSets = the<SourceSetContainer>()

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.jqwik:jqwik:1.8.5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    pitest("org.pitest:pitest-junit5-plugin:1.2.1")
}

application {
    mainClass.set("org.spotifumtp37.Main")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(listOf("org.spotifumtp37.*"))
    targetTests.set(listOf("org.spotifumtp37.*"))
    outputFormats.set(listOf("HTML", "XML"))
    timestampedReports.set(false)
}

val evosuiteTargets = listOf(
    "org.spotifumtp37.util.SongTypeAdapter",
    "org.spotifumtp37.util.SubscriptionPlanAdapter",
    "org.spotifumtp37.util.LocalDateTimeAdapter",
    "org.spotifumtp37.util.JsonDataParser"
)

tasks.register("evosuiteGenerate") {
    group = "verification"
    description = "Generate regression tests with EvoSuite for configured target classes"
    dependsOn(tasks.testClasses)

    doLast {
        val projectCp = sourceSets.named("main").get().runtimeClasspath.files
            .filter { it.exists() }
            .joinToString(File.pathSeparator) { it.absolutePath }
        val workspaceDir = project.projectDir.absolutePath
        val userHome = System.getProperty("user.home")
        evosuiteTargets.forEach { targetClass ->
            exec {
                commandLine(
                    "bash", "-lc",
                    "docker run --rm " +
                            "-v \"$workspaceDir\":/workspace " +
                            "-v \"$userHome\":\"$userHome\" " +
                            "-w /workspace " +
                            "evosuite/evosuite:latest " +
                            "-class $targetClass " +
                            "-projectCP \"$projectCp\" " +
                            "-Dtest_dir=src/test/java " +
                            "-Dassertions=true " +
                            "-Dsearch_budget=60"
                )
            }
        }
    }
}

tasks.register("atsFullPipeline") {
    group = "verification"
    description = "Run ATS full workflow: JUnit + JaCoCo + PIT + EvoSuite"
    dependsOn("test", "jacocoTestReport", "pitest", "evosuiteGenerate")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}