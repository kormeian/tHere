# 베이스 이미지 openjdk11
FROM openjdk:11 AS builder
COPY build/libs/*.jar bepo.jar
# 8080 컨테이너 포트 노출
EXPOSE 8080
# jar 파일 실행
ENTRYPOINT ["java","-jar","/bepo.jar"]