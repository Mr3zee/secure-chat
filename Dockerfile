FROM gradle:7.5-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle server:buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 80:80

ARG db_postgres_host_arg=localhost
ARG db_postgres_port_arg=5678
ARG db_postgres_username_arg=postgres
ARG db_postgres_password_arg=postgres
ARG db_postgres_name_arg=postgres
ARG application_port_arg=80

ENV DB_POSTGRES_HOST=$db_postgres_host_arg
ENV DB_POSTGRES_PORT=$db_postgres_port_arg
ENV DB_POSTGRES_USERNAME=$db_postgres_username_arg
ENV DB_POSTGRES_PASSWORD=$db_postgres_password_arg
ENV DB_POSTGRES_NAME=$db_postgres_name_arg
ENV APPLICATION_PORT=$application_port_arg

RUN mkdir /docker-app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /docker-app/secure-char.jar
ENTRYPOINT ["java","-jar","/docker-app/secure-char.jar"]
