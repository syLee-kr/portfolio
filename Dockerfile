# 기본 이미지 설정 (Java 17 기준)
FROM openjdk:21-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# JAR 파일 복사
COPY build/libs/portpolio-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV DB_URL=jdbc:mysql://34.64.148.224:3306/demo
ENV DB_USERNAME=demo
ENV DB_PASSWORD=demo123