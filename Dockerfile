FROM eclipse-temurin:17-jre as builder
WORKDIR application
COPY ./hep-app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract && rm app.jar

FROM eclipse-temurin:17-jre

ARG TZ
ARG APM
ARG JVM_OPTS
ARG JAVA_OPTS
ENV APM=$APM
ENV JAVA_OPTS=$JAVA_OPTS
ENV JVM_OPTS=$JVM_OPTS
ENV TZ=$TZ

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV JAVA_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENV SPRING_RUN="org.springframework.boot.loader.JarLauncher"

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS $APM $JAVA_DEBUG $JAVA_OPTS $SPRING_RUN"]