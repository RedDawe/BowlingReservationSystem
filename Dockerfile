# STAGE A: Create jar
FROM maven:3.8.5-openjdk-18-slim AS MAVEN_BUILD

# 1. Switch workdir
ENV HOME=/home/usr/app
RUN ["sh", "-c", "mkdir -p ${HOME}"]
WORKDIR $HOME

# 2. Download dependencies
COPY ./pom.xml $HOME/
RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "clean", "package", "--fail-never"]

# 3. Copy the rest and compile
COPY ./ $HOME/
RUN ["mvn", "package"]

# STAGE B: Create minimized image
FROM openjdk:19-ea-5-jdk-alpine3.16

COPY --from=MAVEN_BUILD /home/usr/app/target/BowlingReservationSystem*.jar /BowlingReservationSystem.jar

CMD ["java", "-jar", "BowlingReservationSystem.jar"]
