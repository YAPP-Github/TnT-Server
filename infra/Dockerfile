FROM eclipse-temurin:21-jre

ARG SPRING_ACTIVE_PROFILE
ENV SPRING_ACTIVE_PROFILE=${SPRING_ACTIVE_PROFILE}

COPY build/libs/TnT-0.0.1-SNAPSHOT.jar TnT.jar
COPY src/main/resources/config/agent.java/ agent.java

ENTRYPOINT ["java", "-javaagent:agent.java/scouter.agent.jar", "-Dscouter.config=agent.java/conf/scouter.conf", "-Duser.timezone=Asia/Seoul", "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}", "-jar", "/TnT.jar"]
