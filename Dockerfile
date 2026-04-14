FROM eclipse-temurin:17-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y wget
RUN wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
COPY LibraryManagementSystem.java .
RUN javac -cp .:mysql-connector-j-8.0.33.jar LibraryManagementSystem.java
CMD ["java", "-cp", ".:mysql-connector-j-8.0.33.jar", "LibraryManagementSystem"]