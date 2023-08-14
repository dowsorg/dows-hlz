FROM eclipse-temurin:17-jre as builder
#调整docker
WORKDIR application
ARG JAR_FILE

COPY hep-app/target/${JAR_FILE} app.jar

RUN java -Djarmode=layertools -jar app.jar extract && rm app.jar


FROM eclipse-temurin:17-jre

ARG RTE
#默认dev
ENV RTE=${RTE:-"dev"}
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#配置开启debug
ENV JAVA_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENV SPRING_RUN="org.springframework.boot.loader.JarLauncher"

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $JAVA_DEBUG $SPRING_ENV$RTE $SKYWALKINGE$RTE $SPRING_RUN"]