# Flix Session Service

세션 관리를 위한 마이크로서비스입니다. Redis를 사용하여 세션 데이터를 저장하고 관리합니다.

## 주요 기능

-   세션 생성 및 관리
-   세션 상태 추적
-   세션 만료 시간 관리
-   디바이스 타입 자동 감지 (Web, Mobile, TV)
-   세션 갱신 및 삭제

## 기술 스택

-   Java 17
-   Spring Boot 3.2.3
-   Redis
-   Maven

## API 엔드포인트

### 세션 관리

-   `POST /api/v1/sessions`

    -   새로운 세션 생성
    -   Request Body:
        ```json
        {
            "userId": "string",
            "deviceInfo": "string",
            "ipAddress": "string"
        }
        ```

-   `GET /api/v1/sessions/{sessionId}`

    -   세션 정보 조회

-   `PUT /api/v1/sessions/{sessionId}/refresh`

    -   세션 갱신

-   `DELETE /api/v1/sessions/{sessionId}`
    -   세션 삭제

### 세션 조회

-   `GET /api/v1/sessions/user/{userId}`

    -   사용자의 모든 세션 조회

-   `GET /api/v1/sessions/active`

    -   활성 세션 목록 조회

-   `GET /api/v1/sessions`
    -   모든 세션 목록 조회

### 세션 상태 관리

-   `PUT /api/v1/sessions/{sessionId}/status`
    -   세션 상태 업데이트
    -   Request Body:
        ```json
        {
            "status": "ACTIVE|INACTIVE|EXPIRED"
        }
        ```

## 로컬 개발 환경 설정

1. Redis 서버 실행

    ```bash
    # Docker 사용
    docker run --name redis -p 6379:6379 -d redis

    # 또는 WSL2 사용
    sudo service redis-server start
    ```

2. 환경 변수 설정

    ```bash
    # .env 파일 생성
    CONFIG_LOCAL_HOST=localhost
    CONFIG_LOCAL_PORT=8888
    ```

3. 애플리케이션 실행
    ```bash
    mvn spring-boot:run
    ```

## 세션 데이터 구조

```json
{
    "sessionId": "string",
    "userId": "string",
    "deviceInfo": "string",
    "ipAddress": "string",
    "lastAccessTime": "datetime",
    "expiryTime": "datetime",
    "status": "ACTIVE|INACTIVE|EXPIRED",
    "type": "WEB|MOBILE|TV|OTHER"
}
```

## 세션 타입

-   `WEB`: 웹 브라우저
-   `MOBILE`: 모바일 기기
-   `TV`: TV 또는 스마트 TV
-   `OTHER`: 기타 디바이스

## 세션 상태

-   `ACTIVE`: 활성 세션
-   `INACTIVE`: 비활성 세션
-   `EXPIRED`: 만료된 세션

## 세션 만료 시간

-   기본 만료 시간: 24시간
-   세션 갱신 시 만료 시간이 자동으로 연장됨

## 모니터링

-   Actuator 엔드포인트: `http://localhost:8052/actuator`
-   주요 메트릭:
    -   세션 생성/삭제 수
    -   활성 세션 수
    -   Redis 캐시 히트율
    -   API 응답 시간

## 테스트

```bash
./mvnw test
```

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.
