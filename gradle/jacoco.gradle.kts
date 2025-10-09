val exclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "**/hilt_*/*",
    "**/*Module.class",
    "**/*Module_*",
    "**/*_Impl*",
)

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// Register a JacocoReport task for code coverage analysis
tasks.register<JacocoReport>("jacocoCoverageReport") {
    // Depend on unit tests and Android tests tasks
    dependsOn(listOf("testDebugUnitTest", "connectedDebugAndroidTest"))
    // Set task grouping and description
    group = "Reporting"
    description = "Execute UI and unit tests, generate and combine Jacoco coverage report"
    // Configure reports to generate both XML and HTML formats
    reports {
        html.required.set(true)
    }
    // Set source directories to the main source directory
    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
    // Set class directories to compiled Java and Kotlin classes, excluding specified exclusions
    classDirectories.setFrom(
        files(
            fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
                exclude(exclusions)
            },
            fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
                exclude(exclusions)
            }
        ))
    // Collect execution data from .exec and .ec files generated during test execution
    executionData.setFrom(
        files(
            fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
        ))
    doLast {
        val reportUrl =
            layout.buildDirectory.file("reports/jacoco/jacocoDebugCodeCoverage/html/index.html")
                .get().asFile.toURI()
        println("Jacoco report generated at: file://${reportUrl.path}")
    }
}

tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
    dependsOn("jacocoCoverageReport")
    group = "Verification"
    description = "Check if the code coverage meets the minimum threshold"
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.9".toBigDecimal()
            }
        }

        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.9".toBigDecimal()
            }
        }
    }
    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
    classDirectories.setFrom(
        files(
            fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
                exclude(exclusions)
            },
            fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
                exclude(exclusions)
            }
        ))
    executionData.setFrom(
        files(
            fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
        ))
}