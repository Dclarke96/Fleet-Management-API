FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

RUN JAR_FILE=$(ls build/libs/*.jar | grep -v plain | head -n 1) && cp $JAR_FILE app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]