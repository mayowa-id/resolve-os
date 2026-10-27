FROM eclipse-temurin:25.0.4.1_1-jdk-noble AS build

WORKDIR /workspace
COPY . .
RUN ./mvnw --batch-mode --no-transfer-progress \
    -pl apps/api -am clean package -DskipTests

FROM eclipse-temurin:25.0.4.1_1-jre-noble AS runtime

RUN groupadd --system --gid 10001 resolveos \
    && useradd --system --uid 10001 --gid resolveos --home-dir /opt/resolveos --shell /usr/sbin/nologin resolveos \
    && mkdir -p /opt/resolveos \
    && chown resolveos:resolveos /opt/resolveos

WORKDIR /opt/resolveos
COPY --from=build --chown=resolveos:resolveos \
    /workspace/apps/api/target/resolveos-api-0.1.0-SNAPSHOT.jar application.jar

USER 10001:10001
EXPOSE 10000

ENTRYPOINT ["java", "-jar", "/opt/resolveos/application.jar"]
