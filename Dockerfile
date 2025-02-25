# Create a stage for resolving and downloading dependencies.
FROM eclipse-temurin:21-jdk-jammy AS deps

WORKDIR /build

# Copy the mvnw wrapper with executable permissions.
COPY --chmod=0755 mvnw mvnw
COPY .mvn .mvn/

# Download dependencies as a separate step to take advantage of Docker's caching.
# Leverage a cache mount to /root/.m2 so that subsequent builds don't have to
# re-download packages.
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests

FROM deps AS build

WORKDIR /build

COPY src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

FROM openjdk:24-jdk-slim AS final
LABEL com.kuro.kuroline.author="Jonathan - kurojojo08@gmail.com"
WORKDIR /app

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

RUN mkdir logs
RUN chown appuser:appuser logs

USER appuser

# Copy the built JAR file from the previous stage to the container
COPY --from=build /build/target/app.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]

EXPOSE 8091