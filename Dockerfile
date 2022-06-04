ARG MAVEN_BUILD_WORKDIR=/home/usr/app

# STAGE A: Create jar
FROM maven:3.8.5-openjdk-18-slim AS MAVEN_BUILD

# 1. Switch workdir
ARG MAVEN_BUILD_WORKDIR
ENV HOME=$MAVEN_BUILD_WORKDIR
RUN ["sh", "-c", "mkdir -p $HOME"]
WORKDIR $HOME

# 2. Download maven dependencies
COPY ./pom.xml $HOME/
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "-B", "dependency:resolve-plugins", "dependency:resolve", \
                   "test", "clean", "package", "--fail-never"]

# 3. Download node dependencies
COPY ./src/main/frontend/package*.json $HOME/src/main/frontend/
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "clean", "compile", "--fail-never"]

# 4. Copy the rest and compile
COPY ./ $HOME/
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "clean", "package"]

# STAGE B: Create minimized image
FROM openjdk:19-ea-5-jdk-alpine3.16

ARG MAVEN_BUILD_WORKDIR

COPY --from=MAVEN_BUILD $MAVEN_BUILD_WORKDIR/target/BowlingReservationSystem*.jar /BowlingReservationSystem.jar

CMD ["java", "-jar", "BowlingReservationSystem.jar", "--spring.profiles.active=deployment"]
