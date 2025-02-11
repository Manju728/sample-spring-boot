FROM azul/zulu-openjdk-alpine:17.0.13-17.54
WORKDIR /app

# Copy the built JAR from the build stage
COPY target/*.jar app.jar
COPY cacerts cacerts

EXPOSE 8080
# Set the startup command to run the application
CMD ["java", "-jar", "./app.jar"]
