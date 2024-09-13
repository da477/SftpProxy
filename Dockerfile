FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/SftpProxy.war /app/SftpProxy.war
EXPOSE 8080
CMD ["java", "-jar", "/app/SftpProxy.war", "--server.port=8080"]