
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
}

group = "com.ablil"
version = "0.0.1-SNAPSHOT"
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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
    dependsOn("openApiGenerate")
    dependsOn("setupGithooks")
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
}

openApiGenerate {
    // more config here: https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
    val pkgName = "com.ablil.springstarter.webapi"
    val outDir = "$buildDir/generated/openapi"
    val specsDir = "$projectDir/src/main/resources/specs"

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
            Pair("useTags", "true"),
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
    debug.set(true)
    verbose.set(true)

    additionalEditorconfig.set(mapOf(
        "max_line_length" to "100",
        "ktlint_standard_function-signature" to "disabled",
        "ktlint_standard_multiline-expression-wrapping" to "disabled",
        "ktlint_standard_string-template-indent" to "disabled",
    ))
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
