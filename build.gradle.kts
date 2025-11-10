plugins {
  java
  id("org.springframework.boot") version "3.5.7"
  id("io.spring.dependency-management") version "1.1.7"
  id("jacoco")
  id("com.diffplug.spotless") version "6.25.0"
  id("org.openapi.generator") version "7.17.0"
}

group = "com.impactdryer"

version = "0.0.1-SNAPSHOT"

description = "Demo project for Spring Boot"

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

repositories { mavenCentral() }

dependencies {
  implementation("com.google.guava:guava:33.5.0-jre")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.40")
  implementation("jakarta.validation:jakarta.validation-api:3.1.1")
  implementation("jakarta.validation:jakarta.validation-api:3.1.1")
  implementation("org.openapitools:jackson-databind-nullable:0.2.8")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  compileOnly("org.projectlombok:lombok")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:junit-jupiter:1.20.3")
  testImplementation("org.testcontainers:postgresql:1.20.3")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  runtimeOnly("org.postgresql:postgresql")
  implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.11.0")
}

// Generate coverage report after tests and enforce a minimum coverage on the "check" lifecycle
tasks.withType<Test> {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport) // always generate coverage report after running tests
}

// Configure JaCoCo reports
tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true) // useful for CI (e.g. Sonar)
    html.required.set(true) // browseable report under build/reports/jacoco/test/html
    csv.required.set(false)
  }
}

tasks.withType<JacocoCoverageVerification> {
  dependsOn(tasks.test)
  afterEvaluate {
    classDirectories.setFrom(
      files(
        classDirectories.files
          .map {
            fileTree(it).apply {
              exclude("/**/openapi")
              exclude("/**/openapitools")
            }
          }
          .toList()
      )
    )
  }
  violationRules {
    rule {
      limit {
        counter = "LINE"
        value = "COVEREDRATIO"
        minimum = 0.9.toBigDecimal()
      }
    }
  }
}

tasks.withType<JacocoReport> {
  afterEvaluate {
    classDirectories.setFrom(
      files(
        classDirectories.files
          .map {
            fileTree(it).apply {
              exclude("/**/openapi")
              exclude("/**/openapitools")
            }
          }
          .toList()
      )
    )
  }
}
// Configure Spotless
spotless {
  lineEndings = com.diffplug.spotless.LineEnding.UNIX
  encoding("UTF-8")
  java {
    target("src/*/java/**/*.java")
    importOrder()
    removeUnusedImports()
    trimTrailingWhitespace()
    formatAnnotations()
    cleanthat()
    palantirJavaFormat("2.81.0")
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt("0.47").googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
  }
  format("misc") {
    target("*.md", "*.yml", "*.yaml", "*.properties", "*.xml")
    trimTrailingWhitespace()
    endWithNewline()
  }
  yaml {
    target("src/**/*.yaml")
    jackson()
    prettier()
  }
}

// Make the standard check depend on coverage verification and formatting
tasks.check {
  dependsOn(tasks.jacocoTestCoverageVerification)
  dependsOn("spotlessCheck")
}

val openApiSpecFile =
  layout.projectDirectory.file("src/main/resources/openapi/api-spec.yaml").asFile
val openApiSpecUri = openApiSpecFile.toURI().toASCIIString()
val openApiSpecPathForward = openApiSpecFile.absolutePath.replace("\\", "/")

openApiGenerate {
  generatorName.set("spring")
  // Prefer proper file URI (handles Windows paths); plugin also accepts forward-slash path if
  // needed
  inputSpec.set(openApiSpecUri)
  outputDir.set("$buildDir/generated")
  apiPackage.set("com.impactdryer.deviceapi.infrastructure.openapi")
  modelPackage.set("com.impactdryer.deviceapi.infrastructure.openapi.model")
  configOptions.set(
    mapOf(
      "useSpringBoot3" to "true",
      "delegatePattern" to "true",
      "performBeanValidation" to "true",
      "useSwaggerUI" to "false",
      "apiNameSuffix" to "",
    )
  )
}

sourceSets { named("main") { java { srcDir("$buildDir/generated/src/main/java") } } }

tasks.named("compileJava") { dependsOn("openApiGenerate") }

tasks.named("spotlessJava") { dependsOn("openApiGenerate") }

springBoot { mainClass.set("com.impactdryer.deviceapi.DeviceapiApplication") }

tasks.getByName<Jar>("jar") { enabled = false }
