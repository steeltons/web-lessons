# FROM openjdk:17-slim
# RUN apt-get -y update
# RUN apt-get -y install curl
# RUN apt-get -y install jq
# COPY ./build/libs/todo-*.jar /app.jar
# RUN mkdir -p app/extract && (cd app/extract; jar -xf /app.jar)
# RUN mv /app/extract/BOOT-INF/classes/application-prod.yml /app/extract/BOOT-INF/classes/application.yml
# WORKDIR /app
# RUN mv /app/extract/BOOT-INF/lib /app/lib
# RUN mv /app/extract/META-INF /app/META-INF
# RUN mv /app/extract/BOOT-INF/classes /app
# RUN rm -rf /app/extract
# EXPOSE 7777
# ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.jenjetsu.com.todo.TodoServiceApplication"]
FROM openjdk:17-slim AS builder
COPY ./build/libs/todo-*.jar app.jar
RUN mkdir -p app/extract && (cd app/extract; jar -xf /app.jar)

FROM openjdk:17-slim
VOLUME /tmp
ARG DEPENDENCY=/app/extract
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
RUN apt-get -y update
RUN apt-get -y install curl
RUN apt-get -y install jq
EXPOSE 7777
ENTRYPOINT ["java",  "-cp", "app:app/lib/*", "org.jenjetsu.com.todo.TodoServiceApplication"]