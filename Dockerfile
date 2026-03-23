ARG AWS_LAMBDA_WEB_ADAPTER_VERSION=1.0.0-rc1
FROM public.ecr.aws/awsguru/aws-lambda-adapter:${AWS_LAMBDA_WEB_ADAPTER_VERSION} AS lambda-adapter

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

ENV SERVER_PORT=8080 \
    PORT=8080 \
    AWS_LWA_PORT=8080 \
    AWS_LWA_READINESS_CHECK_PORT=8080 \
    AWS_LWA_READINESS_CHECK_PATH=/actuator/health

COPY --from=lambda-adapter /lambda-adapter /opt/extensions/lambda-adapter
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
