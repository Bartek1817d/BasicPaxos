FROM openjdk:8
ARG name
WORKDIR /home
ADD target/learner-0.0.1-SNAPSHOT-jar-with-dependencies.jar .
ADD src/main/resources/hosts.json .
CMD ["java", "-jar", "learner-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "learner1"]
