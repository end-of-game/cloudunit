FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app
RUN git clone https://github.com/Treeptik/cloudunit-cli.git --depth 1

WORKDIR /usr/src/app/cloudunit-cli
RUN mvn clean package -DskipTests

ENTRYPOINT java -jar target/CloudUnitCLI.jar
