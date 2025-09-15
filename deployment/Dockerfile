FROM gradle:8.5.0-jdk21 AS build
WORKDIR /home/gradle/src

COPY . .

RUN gradle build -x validateStructure -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /home/gradle/src/applications/app-service/build/libs/*.jar ./app.jar

ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

EXPOSE 8082

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
