## THE:ME Backend

3D 기반 공학 교육 플랫폼 백엔드 API

## 주요 기능

JWT 기반 사용자 인증

소셜 로그인 (Google, Kakao, Naver)

AI 어시스턴트 (GPT-5-mini)

3D 모델 에셋 스트리밍 및 메타데이터 API 제공 (New)

스마트 PDF 리포트 생성 (New) ⭐

대화 내역 및 사용자 메모를 PDF 문서로 자동 변환

한글 폰트(HYGoThic) 지원

## 기술 스택

### 1️⃣ 세션 기반 (웹 UI)

이메일/비밀번호 로그인

소셜 로그인 (Google, Kakao, Naver)

브라우저 세션 쿠키 사용

### 2️⃣ JWT 토큰 (REST API)

JWT Access/Refresh Token

API 클라이언트용 (모바일, SPA)

OAuth → JWT 연동 지원 ⭐ NEW

### 🤖 AI 어시스턴트 기능 (NEW!)

OpenAI GPT-4o 기반 컨텍스트 인식 AI

4가지 3D 엔지니어링 모델 지원

Jet Engine (제트 엔진)

Suspension (차량 서스펜션)

Robot Arm (로봇 팔)

Vice (바이스)

부품별 상세 설명 (3D 메시 선택 시)

대화 컨텍스트 유지 (세션별, 객체별)

학부 수준 기술 설명

### 📄 PDF 리포트 기능 (NEW!)

iText 5 + iText Asian 라이브러리 사용

자동 기록: AI와의 대화 내용이 자동으로 포함됨

메모 기능: 사용자가 작성한 학습 메모 포함

### 🔐 보안 기능

Spring Security 7.0.2

BCrypt 비밀번호 암호화

Role 기반 권한 관리 (USER, ADMIN, PREMIUM)

JWT 토큰 인증/갱신

### 📡 API 문서화

Swagger UI: http://localhost:8080/swagger-ui.html

API Docs: http://localhost:8080/api-docs

## 기술 스택 구조

Backend
├── Spring Boot 3.4.2
├── Spring Security 7.0.2
├── Spring Data JPA
├── JWT (JJWT 0.12.3)
├── iText PDF 5.5.13 (PDF Generation)
└── H2 Database

AI & ML
└── OpenAI GPT-5-mini API

Documentation
└── Swagger (SpringDoc OpenAPI 2.3.0)

OAuth 2.0
├── Google OAuth
├── Kakao OAuth
└── Naver OAuth
## 📡 주요 엔드포인트

### 🌐 웹 페이지

GET  /                      → 홈 (로그인 페이지)
GET  /login                 → 로그인 페이지
POST /login                 → 폼 로그인 처리
GET  /signup                → 회원가입 페이지
POST /signup                → 회원가입 처리
GET  /dashboard             → 대시보드 (인증 필요)
GET  /auth/{provider}       → OAuth 로그인 시작
GET  /oauth-signup          → OAuth 추가 정보 입력
### 🔌 인증 API

POST /api/auth/login        → JWT 로그인
POST /api/auth/refresh      → JWT 토큰 갱신
GET  /api/auth/{provider}   → OAuth 소셜 로그인 (JWT) ⭐
### 🤖 AI 어시스턴트 API (NEW!)

POST   /api/ai/ask          → AI에게 질문하기
POST   /api/ai/report       → 대화 내역 PDF 다운로드 ⭐
DELETE /api/ai/history      → 대화 히스토리 삭제
DELETE /api/ai/session      → 세션 전체 삭제
## 📱 OAuth 소셜 로그인 사용법

### 웹 클라이언트 (세션 방식)

JavaScript
// 소셜 로그인 버튼 클릭
window.location.href = '/auth/google';

// 자동으로 세션 생성 후 대시보드로 이동
### API 클라이언트 (JWT 방식) ⭐ NEW

JavaScript
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
Java 17

Spring Boot 3.4.2

Spring Security

H2 Database

OpenAI API

## 시작하기

### 요구사항

JDK 17 이상

Gradle

### 설치

git clone https://github.com/YOUR_USERNAME/SIMVEX-Backend.git cd SIMVEX-Backend

### 설정

application.properties.example 복사 (필수) 보안을 위해 실제 키는 포함되어 있지 않습니다. 복사 후 값을 채워주세요.

cp src/main/resources/application.properties.example src/main/resources/application.properties

API 키 설정 (application.properties)

OpenAI (GPT-5-mini)
openai.api.key=your-key-here

JWT Secret Key
jwt.secret=your-secret-key jwt.access-token-validity=3600000 jwt.refresh-token-validity=86400000

Google OAuth (선택)
sns.google.client.id=your-id sns.google.client.secret=your-secret

Kakao OAuth (선택)
sns.kakao.client.id=your-key

Naver OAuth (선택)
sns.naver.client.id=your-id sns.naver.client.secret=your-secret

### 실행

./gradlew bootRun

서버: http://localhost:8080

## API 문서

Swagger UI: http://localhost:8080/swagger-ui.html

### 주요 엔드포인트 상세

인증

POST /api/auth/signup - 회원가입

POST /api/auth/signin - 로그인

GET /auth/{provider} - OAuth 로그인

3D 모델 & 에셋 (New)

GET /api/objects?category={keyword} - 모델 부품 리스트 및 메타데이터 조회

GET /asset/{category}/{filename} - 3D 파일(.glb) 및 이미지 직접 접근 (로그인 불필요)

AI 어시스턴트 (자동 저장)

POST /api/ai/ask - 질문하기

{ "objectName": "drone", "question": "프로펠러의 역할은?", "sessionId": "user-123", "selectedPart": "impeller_blade" }

PDF 리포트 (New)

POST /api/ai/report - PDF 리포트 다운로드

{ "sessionId": "user-123", "objectName": "drone", "title": "드론 학습 리포트", "memo": "중요한 내용 메모" }

## 지원 3D 모델 (API 키워드)

API 요청 시 category 또는 objectName 파라미터에 아래 소문자 키워드를 사용하세요.

| 모델명 | API 키워드 (category) | | 로봇 팔 | robot_arm | | 머신 바이스 | machine_vice | | 판 스프링 | leaf_spring | | 드론 | drone | | V4 엔진 | v4_engine | | 로봇 집게 | robot_gripper | | 서스펜션 | suspension | | 제트 엔진 | jet_engine |

## 개발

### 빌드

./gradlew build

### 테스트

./gradlew test