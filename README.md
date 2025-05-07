# 쿠폰 발급 이벤트

## 정의
한정된 수량의 쿠폰을 먼저 신청한 사용자에게 제공하는 이벤트

## 쿠폰 이벤트 요구사항
- 이벤트 기간내에(ex 2025-04-28 오후 3시 ~ 2025-04-28 오후 5시) 발급
- 선착순 이벤트는 유저당 1번의 쿠폰 발급
- 선착순 쿠폰의 최대 쿠폰 발급 수량 설정
## 쿠폰 발급 기능
- 쿠폰 발급 기능
  - 쿠폰 발급 기간 검증
  - 쿠폰 발급 수량 검증
    - 쿠폰 전체 발급 수량
    - 중복 발급 요청 검증
  - 쿠폰발급
    - 쿠폰 발급 수량 증가
    - 쿠폰 발급 기록 저장
      - 쿠폰 ID
      - 유저 ID
## 쿠폰 발급 기능 구현의 목표
- 정확한 발급 수량 제어(동시성 이슈 처리)
- 높은 처리량

# 시스템 아키텍쳐
![image](https://github.com/user-attachments/assets/d2142266-52f1-4dd2-b987-d8da0df3ba7e)

### Tech Stack
#### Infra
Aws EC2, Aws RDS, Aws Elastic Cache,

#### Server
Java 17, Spring Boot 3.1, Spring Mvc, JPA, QueryDsl

#### Database
Mysql, Redis, H2

#### Monitoring
Aws Cloud Watch, Spring Actuator, Promethous, Grafana

#### Etc
Locust, Gradle, Docker

![쿠폰 발급 서버 구조 학습-1](https://github.com/user-attachments/assets/19cf08ac-56a5-4296-b691-44746614906e)
![쿠폰 발급 서버 구조 학습-2](https://github.com/user-attachments/assets/cc56774d-b09b-4875-89d6-6cfff61251f4)
![쿠폰 발급 서버 구조 학습-3](https://github.com/user-attachments/assets/8e2638d4-b571-4b60-ba4c-a8d43c95e19e)
![쿠폰 발급 서버 구조 학습-4](https://github.com/user-attachments/assets/ba8ee9bb-194d-4702-9398-823e433a9b85)
![쿠폰 발급 서버 구조 학습-5](https://github.com/user-attachments/assets/8a24d044-ec88-4612-8e23-0b2b440ea4b0)


