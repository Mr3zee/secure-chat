FROM gradle:7.5-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle server:buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
ENV DB_POSTGRES_HOST=host.docker.internal
RUN mkdir /docker-app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /docker-app/secure-char.jar
ENTRYPOINT ["java","-jar","/docker-app/secure-char.jar"]
