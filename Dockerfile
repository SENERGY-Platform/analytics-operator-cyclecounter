FROM maven:3.5-jdk-8-onbuild
COPY api-key /usr/src/app/target/api-key
CMD ["java","-jar","/usr/src/app/target/operator-cyclecounter-jar-with-dependencies.jar"]
