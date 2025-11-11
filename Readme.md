# Device API

A RESTful API for managing network devices and their topology, built using Java and Spring Boot.

## Overview

Device API provides endpoints for:

- Registering and managing network devices
- Querying devices by MAC address and type
- Tracking device connections and hierarchies

### Runtime Requirements

- **Java**: JDK 25
- **Docker**: For running PostgreSQL in a container
- **Bruno**: (Optional) For API documentation and testing

## Starting the application

Use script start.sh

```bash
./start.sh
```


## Open api location
Open api spec is located in `resources/openapi/api-spec.yaml`

Controllers are created using open api generator.


```
openApiGenerate {
  generatorName.set("spring")
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
```
