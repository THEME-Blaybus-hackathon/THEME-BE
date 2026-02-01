# THE:ME Backend

3D 기반 공학 교육 플랫폼 백엔드 API

## 주요 기능

- JWT 기반 사용자 인증
- 소셜 로그인 (Google, Kakao, Naver)
- AI 어시스턴트 (GPT-5-mini)
- 6개 3D 모델 지원

## 기술 스택

- Java 17
- Spring Boot 3.4.2
- Spring Security
- H2 Database
- OpenAI API

## 시작하기

### 요구사항

- JDK 17 이상
- Gradle

### 설치

```bash
git clone https://github.com/YOUR_USERNAME/SIMVEX-Backend.git
cd SIMVEX-Backend
```

### 설정

1. `application.properties.example` 복사
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. API 키 설정
```properties
# OpenAI
openai.api.key=your-key-here

# Google OAuth (선택)
sns.google.client.id=your-id
sns.google.client.secret=your-secret

# Kakao OAuth (선택)
sns.kakao.client.id=your-key

# Naver OAuth (선택)
sns.naver.client.id=your-id
sns.naver.client.secret=your-secret
```

### 실행

```bash
./gradlew bootRun
```

서버: http://localhost:8080

## API 문서

Swagger UI: http://localhost:8080/swagger-ui.html

### 주요 엔드포인트

**인증**
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `GET /auth/{provider}` - OAuth 로그인

**AI 어시스턴트**
- `POST /api/ai/ask` - 질문하기

```json
{
  "objectName": "JET_ENGINE",
  "question": "작동 원리는?",
  "sessionId": "user-123",
  "selectedPart": "turbine_blade"
}
```

## 지원 3D 모델

- V4_ENGINE - V4 엔진
- JET_ENGINE - 제트 엔진
- SUSPENSION - 서스펜션
- ROBOT_ARM - 로봇 팔
- VICE - 바이스
- DRONE - 드론

## 개발

### 빌드

```bash
./gradlew build
```

### 테스트

```bash
./gradlew test
```
