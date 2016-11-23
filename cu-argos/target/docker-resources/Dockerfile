# Set the base image to Java8
FROM java:8
 
# Set the maintainer
MAINTAINER Nicolas MULLER
 
# Add the application
ADD /argos-1.0-SNAPSHOT.jar /
 
# Show java version
RUN java -version
 
# Expose the application port
EXPOSE 8080
 
# Set default container command
ENTRYPOINT ["java", "-jar", "/argos-1.0-SNAPSHOT.jar"]
