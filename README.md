# 📊 요청 플로우 예시 (티켓 예매)

1. 클라이언트 → API Gateway → **booking-service** 호출
2. booking-service → **inventory-service** 에 좌석 Lock 요청
3. 재고 확인 & Lock 성공 → **payment-service** 호출
4. 결제 성공 이벤트 → **notification-service** 로 메시지 발행
5. 알림 발송 → 최종 완료

---

# 🎯 구현 순서

## 1. 공통 인프라 세팅
➡️ 목표: 서비스들을 붙일 "뼈대" 만들기

- **service-registry (Eureka)**
- **api-gateway (Spring Cloud Gateway)**  

---

## 2. 기본 도메인 서비스
➡️ 목표: **MSA에서 서비스 분리 & DB 분리 감각 익히기**

### 2-1. user-service
- 회원가입 / 로그인
- JWT 인증 발급
- Gateway와 연동하여 인증 흐름 검증

### 2-2. event-service
- 공연 / 이벤트 목록 CRUD
- DB 연동 (서비스별 독립 DB 구조 경험)  

---

## 3. 핵심 동시성 포인트
➡️ 목표: **트래픽/동시성 문제 실습 -> 재고 10개인데 100명 동시에 예매 시 어떻게 처리되는지 테스트**

### 3-1. inventory-service(좌석/재고 관리)
- 동시성 이슈 발생 포인트
- 처음엔 단순 CRUD -> 이후 Redis/분산락으로 개선

### 3-2. event-service (예매 처리)
- inventory-service와 협력 : 좌석 예약 -> 결제 단계 전 전달  

---

## 4. 부가 서비스
➡️ 목표: **비동기 이벤트 기반 학습**

### 4-1. payment-service (모의 결제 API와 연결)
- 성공/실패 이벤트 발행 (Kafka, RabbitMQ or 단순 Event Publisher)

### 4-2. notification-service
- 결제 성공 시 이메일/콘솔 알림  

---

## 5. 최종 확장/테스트
- Redis 캐시 적용 (조회 성능 개선)
- 부하 테스트 (Jmeter, Locust)
- 장애 상황 (동시성 충돌, 좌석 초과 예약) 실험
