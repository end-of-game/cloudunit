# Set the base image to Java8
FROM java:8
 
# Set the maintainer
MAINTAINER Nicolas MULLER
 
# Add the application
ADD /@application.jar@ /
 
# Show java version
RUN java -version
 
# Expose the application port
EXPOSE @server.port@
 
# Set default container command
ENTRYPOINT ["java", "-jar", "/@application.jar@"]
