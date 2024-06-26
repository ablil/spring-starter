import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    id("org.jlleitschuh.gradle.ktlint") version "12.0.2"
    id("org.openapi.generator") version "7.1.0"
    id("com.google.cloud.tools.jib") version "3.4.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"
    kotlin("kapt") version "1.9.24"
}

group = "com.ablil"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.testcontainers:postgresql:1.19.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "17"
    }
    dependsOn("openApiGenerate")
    dependsOn("setupGithooks")
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "integration,test")
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named("generateGitProperties") {
    dependsOn("setupGithooks")
}

tasks.named("runKtlintCheckOverMainSourceSet") {
    dependsOn("openApiGenerate")
}


openApiGenerate {
    // more config here: https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
    val pkgName = "com.ablil.springstarter.webapi"
    val outDir = "$buildDir/generated/openapi"
    val specsDir = "$projectDir/src/main/resources/static"

    generatorName.set("kotlin-spring")
    inputSpecRootDirectory.set(specsDir)
    modelPackage.set("$pkgName.model")
    apiPackage.set("$pkgName.api")
    packageName.set(pkgName)
    outputDir.set(outDir)

    configOptions.putAll(
        mapOf(
            Pair("useSpringBoot3", "true"), // In order to use jakarta.validation instead of javax.validation
            Pair("interfaceOnly", "true"),
            Pair("skipDefaultInterface", "true"), // Do not generate a default implementation for interface methods
            Pair("useTags", "true"), // generate a controller for each tag
            Pair("enumPropertyNaming", "UPPERCASE")
        ),
    )

    cleanupOutput.set(true)
    generateApiTests.set(false)
}

sourceSets {
    main {
        kotlin {
            // include generated sources from openapi generator in the main source set
            srcDir("$buildDir/generated/openapi/src/main/kotlin")
        }
    }
}

ktlint {
    version.set("1.2.0")
    debug.set(true)
    verbose.set(true)

    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "120",
            "ktlint_function_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to "4",
            "ktlint_standard_multine-expression-wrapping" to "disabled",
            "ktlint_standard_function-signature" to "disabled",
            "ktlint_standard_multiline-expression-wrapping" to "disabled",
            "ktlint_standard_no-wildcard-imports" to "disabled",
            "ktlint_standard_filename" to "disabled"
        )
    )
    filter {
        exclude("**/*.gradle.kts")
        exclude { it.file.path.contains("build/generated") }
    }
}

jib {
    to {
        image = "spring-starter"
    }
    container {
        ports = listOf("8080")
        creationTime.set(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")))
    }
}

tasks.bootRun {
    args = listOf("--spring.profiles.active=local")
}

tasks.register<Copy>("setupGithooks") {
    destinationDir = projectDir.resolve(".git/hooks")
    from(projectDir.resolve("local/githooks"))

    doLast {
        println("Copied Git hooks successfully")
    }
}

springBoot {
    buildInfo()
}

// improve the speed of kapt plugin by running their tasks in parallel
// https://kotlinlang.org/docs/kapt.html#improve-the-speed-of-builds-that-use-kapt
tasks.withType<org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask>()
    .configureEach { kaptProcessJvmArgs.add("-Xmx512m") }
