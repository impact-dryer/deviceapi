FROM eclipse-temurin:25-jdk-alpine AS builder

ENV TZ=UTC

WORKDIR /workspace

COPY . .

RUN ./gradlew clean bootJar --no-daemon --stacktrace

RUN JAR_FILE=$(ls build/libs/*.jar) && \
    cp "$JAR_FILE" application.jar

FROM gcr.io/distroless/java25-debian13:nonroot AS distroless-runtime

WORKDIR /app

COPY --from=builder /workspace/application.jar ./application.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","application.jar"]
