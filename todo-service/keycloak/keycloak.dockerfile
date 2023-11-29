FROM openjdk:17-alpine
RUN apk update && apk add bash
ENV KC_HEALTH_ENABLED=true
ENV KC_INFO_ENABLED=true
COPY ./keycloak-22.0.5 /keycloak
EXPOSE 8080
EXPOSE 8443
ENTRYPOINT ["/keycloak/bin/kc.sh", "-v", "start"]
