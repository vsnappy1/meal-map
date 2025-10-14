val exclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    // Specifically for data module
    "**/hilt_*/*",
    "**/*Module.class",
    "**/*Module_*",
    "**/*_Impl*",
    // Specifically for app module
    "**/dagger*/*",
    "**/Hilt_*",
    "**/ComposableSingletons*.*",
    "**/*ScreenKt.class",
    "**/*ScreenKt$*.class",
    "**/ui/components/*",
    "**/ui/theme/*",
    "**/ui/navigation/*",
    "**/ContextUtils*.*",
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

    val allProjects = subprojects + project

    sourceDirectories.setFrom(files(allProjects.map { p ->
        listOf(
            p.layout.projectDirectory.dir("src/main/java"),
            p.layout.projectDirectory.dir("src/main/kotlin")
        )
    }))

    classDirectories.setFrom(files(allProjects.map { p ->
        listOf(
            fileTree(p.layout.buildDirectory.dir("intermediates/javac")) {
                exclude(exclusions)
            },
            fileTree(p.layout.buildDirectory.dir("tmp/kotlin-classes")) {
                exclude(exclusions)
            }
        )
    }))

    executionData.setFrom(files(allProjects.map { p ->
        fileTree(p.layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
    }))

    doLast {
        val reportUrl =
            layout.buildDirectory.file("reports/jacoco/jacocoCoverageReport/html/index.html")
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
                minimum = "0.85".toBigDecimal()
            }
        }

        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
    }

    val allProjects = subprojects + project

    sourceDirectories.setFrom(files(allProjects.map { p ->
        listOf(
            p.layout.projectDirectory.dir("src/main/java"),
            p.layout.projectDirectory.dir("src/main/kotlin")
        )
    }))

    classDirectories.setFrom(files(allProjects.map { p ->
        listOf(
            fileTree(p.layout.buildDirectory.dir("intermediates/javac")) {
                exclude(exclusions)
            },
            fileTree(p.layout.buildDirectory.dir("tmp/kotlin-classes")) {
                exclude(exclusions)
            }
        )
    }))

    executionData.setFrom(files(allProjects.map { p ->
        fileTree(p.layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
    }))
}