# SftpProxy

## Overview
Sftp Proxy enables the acceptance and transmission of files through SFTP via HTTP, catering to applications without native SFTP support. It facilitates the seamless exchange of files with SFTP servers.

## Dependencies
- **Spring Boot Starters:** Web
- **Additional Dependencies:** Lombok, Tomcat Starter, Spring Boot Starter Test, Commons IO, JSch

## Image Build and Container Launch

### Building the Docker Image

The Docker image for the application is built using Maven's Spring Boot plugin.

```bash
mvn spring-boot:build-image
```
After building the image, you can start the container using Docker Compose. Execute the following command in the project directory:

```bash
docker-compose up -d
```

### Contributions

Welcome via issues, pull requests, or feedback.

