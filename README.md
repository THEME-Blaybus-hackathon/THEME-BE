# 🚀 Spring Boot 인증/인가 + AI 어시스턴트 시스템

JWT + OAuth2.0 + OpenAI GPT-4o 기반 통합 플랫폼 (해커톤 준비용)

---

## ⚡ 빠른 시작

```bash
# 1. 프로젝트 빌드
./gradlew clean build

# 2. 환경 변수 설정 (필수!)
export OPENAI_API_KEY=sk-proj-xxxxx

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. 브라우저에서 접속
http://localhost:8080
```

---

## 📚 문서 목록

### 🔧 설정 가이드
- **[PROJECT_SETUP.md](PROJECT_SETUP.md)** ⭐ - **팀원용 환경 설정 가이드 (필독!)**
  - 개발 환경 요구사항
  - 프로젝트 시작 방법
  - OAuth 클라이언트 등록
  - 프로젝트 구조 설명
  - 트러블슈팅

### 📱 API 문서
- **[API_OAUTH_GUIDE.md](API_OAUTH_GUIDE.md)** ⭐ - **OAuth → JWT 연동 가이드**
  - 모바일/SPA용 소셜 로그인 구현
  - 전체 플로우 및 예시 코드
  - React Native, Android, iOS 예제
  - 토큰 저장 및 갱신 방법

- **[AI_ASSISTANT_API.md](AI_ASSISTANT_API.md)** 🤖 - **AI 어시스턴트 API 가이드 (신규!)**
  - 3D 엔지니어링 모델 AI 설명
  - 컨텍스트 인식 대화 시스템
  - 4가지 모델 지원 (Jet Engine, Suspension, Robot Arm, Vice)
  - 프론트엔드 통합 가이드

### 🔐 OAuth 설정
- [KAKAO_SETUP.md](KAKAO_SETUP.md) - 카카오 로그인 설정 가이드
- [KAKAO_401_FIX.md](KAKAO_401_FIX.md) - 카카오 401 에러 해결
- [KAKAO_EMAIL_REMOVED.md](KAKAO_EMAIL_REMOVED.md) - 카카오 이메일 처리

### 🎯 개발 참고
- [OAUTH_SIGNUP_IMPROVEMENT.md](OAUTH_SIGNUP_IMPROVEMENT.md) - OAuth 회원가입 개선 내역
- [HACKATHON_CHECKLIST.md](HACKATHON_CHECKLIST.md) - 해커톤 체크리스트

---

## 🎯 주요 기능

### ✅ 인증 방식 (2가지)

#### 1️⃣ 세션 기반 (웹 UI)
- 이메일/비밀번호 로그인
- 소셜 로그인 (Google, Kakao, Naver)
- 브라우저 세션 쿠키 사용

#### 2️⃣ JWT 토큰 (REST API)
- JWT Access/Refresh Token
- API 클라이언트용 (모바일, SPA)
- **OAuth → JWT 연동 지원** ⭐ NEW

### 🤖 AI 어시스턴트 기능 (NEW!)
- **OpenAI GPT-4o** 기반 컨텍스트 인식 AI
- **4가지 3D 엔지니어링 모델** 지원
  - Jet Engine (제트 엔진)
  - Suspension (차량 서스펜션)
  - Robot Arm (로봇 팔)
  - Vice (바이스)
- **부품별 상세 설명** (3D 메시 선택 시)
- **대화 컨텍스트 유지** (세션별, 객체별)
- **학부 수준 기술 설명**

### 🔐 보안 기능
- Spring Security 7.0.2
- BCrypt 비밀번호 암호화
- Role 기반 권한 관리 (USER, ADMIN, PREMIUM)
- JWT 토큰 인증/갱신

### 📡 API 문서화
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

---

## 🛠 기술 스택

```
Backend
├── Spring Boot 3.4.2
├── Spring Security 7.0.2
├── Spring Data JPA
├── JWT (JJWT 0.12.3)
└── H2 Database

AI & ML
└── OpenAI GPT-5-mini API

Documentation
└── Swagger (SpringDoc OpenAPI 2.3.0)

OAuth 2.0
├── Google OAuth
├── Kakao OAuth
└── Naver OAuth
```

---

## 📡 주요 엔드포인트

### 🌐 웹 페이지
```
GET  /                      → 홈 (로그인 페이지)
GET  /login                 → 로그인 페이지
POST /login                 → 폼 로그인 처리
GET  /signup                → 회원가입 페이지
POST /signup                → 회원가입 처리
GET  /dashboard             → 대시보드 (인증 필요)
GET  /auth/{provider}       → OAuth 로그인 시작
GET  /oauth-signup          → OAuth 추가 정보 입력
```

### 🔌 인증 API
```
POST /api/auth/login        → JWT 로그인
POST /api/auth/refresh      → JWT 토큰 갱신
GET  /api/auth/{provider}   → OAuth 소셜 로그인 (JWT) ⭐
```

### 🤖 AI 어시스턴트 API (NEW!)
```
POST   /api/ai/ask          → AI에게 질문하기
DELETE /api/ai/history      → 대화 히스토리 삭제
DELETE /api/ai/session      → 세션 전체 삭제
```

---

## 📱 OAuth 소셜 로그인 사용법

### 웹 클라이언트 (세션 방식)
```javascript
// 소셜 로그인 버튼 클릭
window.location.href = '/auth/google';

// 자동으로 세션 생성 후 대시보드로 이동
```

### API 클라이언트 (JWT 방식) ⭐ NEW
```javascript
// type=api 파라미터 추가
window.location.href = '/api/auth/google?type=api';

// JSON 응답으로 JWT 토큰 반환
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer"
  }
}
```

**자세한 내용:** [API_OAUTH_GUIDE.md](API_OAUTH_GUIDE.md)

---

## 🏗 프로젝트 구조

```
src/main/java/com/example/Project/
├── config/
│   ├── SecurityConfig.java          # Spring Security 설정
│   └── SwaggerConfig.java            # Swagger 설정
├── controller/
│   ├── AuthController.java           # 웹 로그인/회원가입
│   ├── OauthController.java          # OAuth 콜백 (세션 + JWT)
│   └── api/
│       └── AuthApiController.java    # JWT API
├── security/
│   ├── JwtTokenProvider.java         # JWT 생성/검증
│   └── JwtAuthenticationFilter.java  # JWT 필터
├── service/
│   ├── AuthService.java              # 인증 비즈니스 로직
│   ├── OauthService.java             # OAuth 서비스
│   └── social/
│       ├── GoogleOauth.java          # Google OAuth
│       ├── KakaoOauth.java           # Kakao OAuth
│       └── NaverOauth.java           # Naver OAuth
├── entity/
│   └── User.java                     # 사용자 엔티티
└── dto/                              # 데이터 전송 객체
```

---

## 🔑 환경 변수 설정

`src/main/resources/application.properties` 파일에서 아래 정보 설정:

```properties
# Google OAuth
google.client.id=YOUR_GOOGLE_CLIENT_ID
google.client.secret=YOUR_GOOGLE_CLIENT_SECRET

# Kakao OAuth
kakao.client.id=YOUR_KAKAO_REST_API_KEY
kakao.client.secret=YOUR_KAKAO_CLIENT_SECRET  # (Optional)

# Naver OAuth
naver.client.id=YOUR_NAVER_CLIENT_ID
naver.client.secret=YOUR_NAVER_CLIENT_SECRET

# JWT Secret (Production에서 변경 필수!)
jwt.secret=YOUR_SECRET_KEY_BASE64
```

**자세한 설정:** [PROJECT_SETUP.md](PROJECT_SETUP.md)

---

## 🧪 테스트

### Swagger UI 사용
```
http://localhost:8080/swagger-ui.html
```

### H2 데이터베이스 콘솔
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (비워두기)
```

---

## 👥 팀원 온보딩

1. **[PROJECT_SETUP.md](PROJECT_SETUP.md)** 읽기 (필수)
2. Java 17 설치 확인
3. OAuth 클라이언트 ID/Secret 설정
4. `./gradlew bootRun` 실행
5. Swagger UI에서 API 테스트

**API 클라이언트 개발:** [API_OAUTH_GUIDE.md](API_OAUTH_GUIDE.md) 참고

---

## 📋 해커톤 체크리스트

- [x] JWT 인증 시스템 ✅
- [x] OAuth 소셜 로그인 (Google, Kakao, Naver) ✅
- [x] **OAuth → JWT 연동** ✅ NEW
- [x] Swagger API 문서화 ✅
- [x] Role 기반 권한 관리 ✅
- [x] 토큰 갱신 기능 ✅
- [x] 신규/기존 사용자 분기 처리 ✅

**자세한 내용:** [HACKATHON_CHECKLIST.md](HACKATHON_CHECKLIST.md)

---

## 🐛 트러블슈팅

문제 발생 시:
1. 콘솔 로그 확인
2. H2 콘솔에서 DB 상태 확인
3. Swagger UI로 API 테스트
4. 관련 문서 참고

**일반적인 문제:**
- OAuth 401 에러 → [KAKAO_401_FIX.md](KAKAO_401_FIX.md)
- 빌드 실패 → `./gradlew clean build --refresh-dependencies`
- JWT 파싱 에러 → JJWT 버전 확인 (0.12.x)

---

## 📞 문의

프로젝트 관련 문의사항은 팀 채널에 공유해주세요!

---

**마지막 업데이트:** 2026년 1월 30일  
**버전:** 2.0.0 (OAuth → JWT 연동 추가)  
**License:** MIT

**Happy Hacking! 🎉**
