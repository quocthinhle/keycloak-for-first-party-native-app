# Builder stage, we have a step for building the JAR file
# FROM maven:3.8-amazoncorretto-17 AS build
# ENV EXTENSION_DIR=/src/java-extension/keycloak-seenow-extension
# COPY src $EXTENSION_DIR/src
# COPY pom.xml $EXTENSION_DIR
# RUN mvn -f $EXTENSION_DIR/pom.xml clean install

# Extending keycloak image, then we copy the JAR we've just built, then running new image to test.
# FROM quay.io/keycloak/keycloak:22.0.0
# WORKDIR /opt/keycloak/providers
# COPY --from=build /src/java-extension/keycloak-seenow-extension/target/keycloak-seenow-extension.jar ./keycloak-seenow-extension.jar
# COPY --from=build /src/java-extension/keycloak-seenow-extension/target/dependency/jose4j-0.9.4.jar ./jose4j-0.9.4.jar
# COPY --from=build /src/java-extension/keycloak-seenow-extension/target/dependency/okhttp-2.7.5.jar ./okhttp-2.7.5.jar
# ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

ARG KEYCLOAK_VERSION=22.0.0

FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION}

ARG KC_HOME_DIR=/opt/keycloak

COPY ./kc-extension/target/*.jar  ${KC_HOME_DIR}/providers/
COPY ./kc-extension/target/dependency/okhttp-2.7.5.jar ${KC_HOME_DIR}/providers/okhttp-2.7.5.jar
COPY ./kc-extension/target/dependency/jose4j-0.9.4.jar ${KC_HOME_DIR}/providers/jose4j-0.9.4.jar


RUN ${KC_HOME_DIR}/bin/kc.sh build