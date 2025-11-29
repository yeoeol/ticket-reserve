# 🔄 전체 플로우 예시 (사용자 입장)

1. 전체 이벤트 목록 조회
2. 특정 이벤트 세부 사항 조회
3. 예매하기 클릭 (로그인된 사용자만 이후 접근 가능)
4. 전체 좌석 조회
5. 특정 좌석 선택 후 완료 버튼 클릭 시 결제 페이지 이동
6. 결제 플로우 실행
7. 예매 완료

---

# 📊 요청 플로우 예시 (티켓 예매)

1. 클라이언트 → API Gateway → **inventory-service** 호출
2. inventory-service → **reservation-service** 대기열 시스템 적용
3. 재고 확인 & Lock 성공 → **payment-service** 좌석 별 분산락 적용

---

# 🎯 구현 순서

## 1. 공통 인프라 세팅
➡️ 목표: 서비스들을 붙일 "뼈대" 만들기

- **service-registry (Eureka)**
- **api-gateway (Spring Cloud Gateway)**  
- **config-server (Spring Cloud Config Server)**

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
➡️ 목표: **트래픽/동시성 문제 실습 -> 좌석(재고) 10개인데 100명 동시에 예매 시 어떻게 처리되는지 테스트**

### 3-1. inventory-service(좌석/재고 관리)
- 동시성 이슈 발생 포인트
  - 좌석 목록 화면 입장 
  - 좌석 선점
- 처음엔 단순 CRUD -> 이후 Redis/분산락으로 개선
  - 분산락(redisson) + AOP 적용

---

## 4. 부가 서비스
➡️ 목표: **비동기 이벤트 기반 학습**

### 4-1. payment-service (모의 결제 API와 연결)
- 성공/실패 이벤트 발행 (Kafka)

### 4-2. event-service (공연 생성 시 좌석 생성)
- 공연 생성 이벤트 발행 (inventory-service는 consumer로, 좌석 생성)

---

## 5. 최종 확장/테스트
- Redis 캐시 적용 (조회 성능 개선)
- 부하 테스트 (Jmeter, Locust)
- 장애 상황 (동시성 충돌, 좌석 초과 예약) 실험
