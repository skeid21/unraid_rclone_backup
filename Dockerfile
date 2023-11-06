FROM eclipse-temurin:17-alpine as build

COPY gradlew settings.gradle.kts /
COPY gradle /gradle/
COPY app /app/
RUN ./gradlew installDist

FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add rclone
COPY --from=build /app/build/install/app /app
ENTRYPOINT /app/bin/app



