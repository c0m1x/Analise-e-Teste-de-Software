plugins {
    id("java")
    id("application")
    id("jacoco")
    id("info.solidsoft.pitest") version "1.15.0"
}

import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
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

tasks.named<JavaCompile>("compileJava") {
    options.release.set(8)
}

tasks.named<JavaCompile>("compileTestJava") {
    exclude("**/*_ESTest.java", "**/*_ESTest_scaffolding.java")
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*_ESTest*", "**/*_ESTest_scaffolding*")
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
    "org.spotifumtp37.util.LocalDateTimeAdapter"
)

tasks.register("evosuiteGenerate") {
    group = "verification"
    description = "Generate regression tests with EvoSuite for configured target classes"
    dependsOn(tasks.testClasses)

    doLast {
        val evosuiteClasspathDir = layout.buildDirectory.dir("evosuite-classpath").get().asFile
        delete(evosuiteClasspathDir)
        evosuiteClasspathDir.mkdirs()

        val projectCp = sourceSets.named("main").get().runtimeClasspath.files
            .filter { it.exists() }
            .joinToString(File.pathSeparator) { entry ->
                if (entry.toPath().startsWith(project.projectDir.toPath())) {
                    "/workspace/${entry.relativeTo(project.projectDir).invariantSeparatorsPath}"
                } else {
                    val copiedEntry = File(evosuiteClasspathDir, entry.name)
                    copy {
                        from(entry)
                        into(if (entry.isDirectory) copiedEntry else evosuiteClasspathDir)
                    }
                    "/workspace/${copiedEntry.relativeTo(project.projectDir).invariantSeparatorsPath}"
                }
            }
        val workspaceDir = project.projectDir.absolutePath
        evosuiteTargets.forEach { targetClass ->
            exec {
                commandLine(
                    "docker", "run", "--rm",
                    "-v", "$workspaceDir:/workspace",
                    "-w", "/workspace",
                    "evosuite/evosuite:latest",
                    "-class", targetClass,
                    "-projectCP", projectCp,
                    "-Dtest_dir=src/test/java",
                    "-Dassertions=true",
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
