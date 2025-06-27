## 1. Gradle로 빌드해서 JAR만 추출
#FROM gradle:8.5.0-jdk17 AS build
#COPY . /app
#WORKDIR /app
#RUN gradle build --no-daemon
#
## 2. 실제 실행 컨테이너 (최소 JDK만)
#FROM openjdk:17-jdk-slim
#COPY --from=build /app/build/libs/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app.jar"]
