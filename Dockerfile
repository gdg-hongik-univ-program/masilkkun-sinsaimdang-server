FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar app.jar

# 어떤 프로필을 사용할지 환경변수로 받도록 설정
# 서버 실행 시점에 -Dspring.profiles.active=prod 같은 옵션을 주기 위함입니다.
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app.jar"]