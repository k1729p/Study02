FROM maven AS maven_tool
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn clean install

FROM eclipse-temurin:23
COPY --from=maven_tool /tmp/target/Study02-1.0.0-SNAPSHOT.jar application.jar
COPY docker-config/scripts/wait_for_mongo.sh /wait_for_mongo.sh
RUN apt-get update && apt-get install -y netcat-openbsd && chmod +x /wait_for_mongo.sh
ENTRYPOINT ["/wait_for_mongo.sh", "mongo", "27017", "--", "java", "-jar", "application.jar"]

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:8080/healthcheck || exit 1
  
