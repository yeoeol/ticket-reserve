# 🛠 Tech Stack
![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?style=flat-square&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-%236DB33F.svg?style=flat-square&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-%236DB33F.svg?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-%236DB33F.svg?style=flat-square&logo=springsecurity&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005F0F.svg?style=flat-square&logo=thymeleaf&logoColor=white)

![MySQL](https://img.shields.io/badge/MySQL-%234479A1.svg?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-%23FF4438.svg?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%232496ED.svg?style=flat-square&logo=docker&logoColor=white)

![Apache_Kafka](https://img.shields.io/badge/Apache_Kafka-%23231F20.svg?style=flat-square&logo=apachekafka&logoColor=#231F20)

![Git](https://img.shields.io/badge/git-%23F05032.svg?style=flat-square&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23181717.svg?style=flat-square&logo=github&logoColor=white)

# ⭐ 기술적 의사결정
- **MSA & Gateway**: 마이크로서비스 아키텍처 기반의 서비스 분리 및 Gateway를 통한 단일 진입점 구축
- **Event-Driven (Kafka)**: Kafka를 활용한 서비스 간 느슨한 결합(Loose Coupling) 및 비동기 이벤트 처리
- **Concurrency Control (Redisson)**: AOP와 Redis 분산 락을 활용한 대용량 트래픽 동시성 제어 및 데이터 정합성 보장
- **Security (JWT)**: Stateless한 JWT 기반 인증/인가 프로세스 및 보안 필터 체인 구현
- **Payment Integration**: 외부 PG사(Toss) 연동 및 결제 트랜잭션 관리

---

# 🔄 전체 플로우 예시 (사용자 입장)

1. 전체 버스킹 목록 조회
2. 특정 버스킹 세부 사항 조회
3. 구독하기 클릭 -> 구독 완료 상태 변경
4. 버스킹 시작 1시간 전, 구독자에게 알림 발송

---

# 🎯 구현 순서

## 1. 공통 인프라 세팅
➡️ 목표: 서비스들을 붙일 "뼈대" 만들기

- **service-registry (Eureka)**
- **api-gateway (Spring Cloud Gateway)**  
- **config-server (Spring Cloud Config Server)** (보류)

---

## 2. 기본 도메인 서비스
➡️ 목표: **MSA에서 서비스 분리 & DB 분리 감각 익히기**

### 2-1. user-service
- 회원가입 / 로그인
- JWT 인증 발급
- Gateway와 연동하여 인증 흐름 검증

### 2-2. busking-service
- 공연 / 이벤트 목록 CRUD
- DB 연동

---

## 3. 부가 서비스
➡️ 목표: **비동기 이벤트 기반 학습**

### 3-1. busking-service (공연 생성 시 좌석 생성)
- 버스킹 생성 이벤트 발행 (hot-busking-service는 consumer로, 인기글 데이터 생성)
  - consumer: 인기 버스킹 서비스는 인기글 데이터를 위해 버스킹 생성 이벤트를 구독
  - consumer: 알림 서비스는 주변 사용자에게 새로운 버스킹 생성 알림을 위해 버스킹 생성 이벤트를 구독
  - consumer: 구독 서비스는 알림 발송을 위한 버스킹 정보를 위해 버스킹 생성 이벤트를 구독

---

## 4. 최종 확장/테스트
- Redis 캐시 적용 (조회 성능 개선)
- 부하 테스트 (Jmeter, Locust)
- 장애 상황 (동시성 충돌) 실험

---
# ⬜ TODO
- 부하 테스트
- 단위 테스트
- 동시 요청 성능 분석(테스트 코드) - 낙관적/비관적/분산 락 비교

---
## 아키텍처
<img width="895" height="498" alt="busking-draw_1" src="https://github.com/user-attachments/assets/11960ae7-2ae2-4c81-aa37-68319f83748e" />

아키텍처 추후 수정 예정.

## ERD
추후 추가 예정.

