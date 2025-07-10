FROM gradle:8.14.3 as builder

COPY build.gradle.kts .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY gradle/ ./gradle/
COPY src ./src

RUN gradle installDist

FROM eclipse-temurin:21-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=builder /home/gradle/build/install/super-shiharai-kun /app/
WORKDIR /app/bin
CMD ["./super-shiharai-kun"]
