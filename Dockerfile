FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY mysql-connector-j-9.6.0.jar .
COPY LibraryManagementSystem.java .
RUN javac -cp .:mysql-connector-j-9.6.0.jar LibraryManagementSystem.java
CMD ["java", "-cp", ".:mysql-connector-j-9.6.0.jar", "LibraryManagementSystem"]
# CMD ["bash"]