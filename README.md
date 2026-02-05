# THE:ME Backend

3D 기반 공학 교육 플랫폼 THE:ME의 백엔드 API 서버입니다. 사용자는 3D 모델을 탐색하며 AI 어시스턴트와 실시간으로 대화하고, 대화 내용을 바탕으로 생성된 퀴즈를 풀며, 이 모든 학습 과정(요약+메모+퀴즈 결과)을 스마트 PDF 리포트로 저장할 수 있습니다.

## 주요 기능

🔐 JWT & OAuth 인증: 세션(Web) 및 JWT(API) 통합 인증 지원 (Google, Kakao, Naver).

🤖 AI 어시스턴트 (GPT-5-mini): 3D 엔지니어링 모델에 특화된 컨텍스트 인식 질의응답.

🎯 AI 맞춤형 퀴즈 (New) ⭐: AI와 나눈 대화 내용을 분석하여 OX 퀴즈를 자동 생성하고, 채점 결과와 해설을 오답 노트로 저장.

📄 AI 스마트 요약 리포트 (New) ⭐: 대화 요약 + 학습 메모 + 퀴즈 오답 노트를 통합하여 하나의 PDF 학습 리포트로 자동 생성.

📦 3D 모델 에셋 스트리밍: 3D 모델 파일(.glb) 및 메타데이터 API 제공.

📝 학습 메모: 부품별 사용자 학습 메모 저장 및 리포트 반영.

🛡️ 강력한 보안: Spring Security 7.0.2 기반 Role 관리 및 암호화.

## 기술 스택

### Backend Core

Java 17 / Spring Boot 3.4.2

Spring Security 7.0.2 (BCrypt, Role Management)

Spring Data JPA / H2 Database

### Auth & Security

JWT (JJWT 0.12.3): Access/Refresh Token 발급

OAuth 2.0 Client: Google, Kakao, Naver 연동

### AI & Data Processing

OpenAI API: GPT-5-mini / GPT-4o (Chat, Summarization, Quiz Generation)

iText PDF 5.5.13: PDF Generation

iText Asian: 한글 폰트(HYGoThic) 렌더링 지원

## 기술 스택 구조

Backend

Spring Boot 3.4.2

Spring Security 7.0.2

Spring Data JPA

JWT (JJWT 0.12.3)

iText PDF 5.5.13 (PDF Generation)

H2 Database

AI & ML

OpenAI GPT-5-mini API

Documentation

Swagger (SpringDoc OpenAPI 2.3.0)

OAuth 2.0

Google OAuth

Kakao OAuth

Naver OAuth

## 📡 주요 엔드포인트 (API Endpoints)

### 🌐 웹 페이지 (Web View) GET / : 홈 (로그인 페이지) GET /dashboard : 대시보드 (인증 필요) GET /auth/{provider} : OAuth 로그인 (Google, Kakao, Naver)

### 🔌 인증 API (Auth API) POST /api/auth/login : JWT 로그인 POST /api/auth/refresh : 토큰 갱신 GET /api/auth/{provider}?type=api : OAuth 소셜 로그인 (JWT 반환)

### 🤖 AI 어시스턴트 & Report (New!) POST /api/ai/ask : AI에게 질문하기 (부품/전체) POST /api/ai/summary : 대화 내용 AI 요약 (텍스트 반환) ⭐ NEW POST /api/ai/report : AI 요약 + 메모 + 퀴즈 결과 포함 PDF 다운로드 ⭐ DELETE /api/ai/session : 대화 세션 초기화

### 🎯 AI Quiz API (New!) POST /api/quiz/generate-from-chat : 대화 기반 OX 퀴즈 생성 POST /api/quiz/submit : 퀴즈 답안 제출 & 채점 (PDF 연동 자동 저장)

### 📦 3D Assets & Metadata GET /api/objects : 카테고리별 부품 메타데이터 조회 GET /asset/{category}/{filename} : 3D 모델(.glb) 및 이미지 파일 스트리밍

## 📱 OAuth 소셜 로그인 사용법

### 웹 클라이언트 (세션 방식) 소셜 로그인 버튼 클릭 시 이동: window.location.href = '/auth/google'; -> 로그인 후 대시보드 리다이렉트

### API 클라이언트 (JWT 방식) type=api 파라미터 추가: window.location.href = '/api/auth/google?type=api';

JSON 응답 예시: { "success": true, "data": { "accessToken": "eyJhbGc...", "refreshToken": "eyJhbGc...", "tokenType": "Bearer" } }

## 시작하기 (Getting Started)

### 요구사항

JDK 17 이상

Gradle 7.x 이상

### 설치 git clone https://github.com/YOUR_USERNAME/THE-ME-Backend.git cd THE-ME-Backend

### 설정 (application.properties) OpenAI API Key (GPT-5-mini) openai.api.key=your-key-here

JWT Secret jwt.secret=your-secret-key

OAuth Keys (Optional) sns.google.client.id=your-id sns.google.client.secret=your-secret

### 실행 ./gradlew bootRun

서버 주소: http://localhost:8080 Swagger UI: http://localhost:8080/swagger-ui.html

## 지원 3D 모델 (API Keywords)

드론 (drone): 프로펠러, 배터리 등 구조 학습

로봇 팔 (robot_arm): 관절 및 작동 원리

로봇 집게 (robot_gripper): 그리퍼 작동 메커니즘

V4 엔진 (v4_engine): 내연기관 엔진 구조

서스펜션 (suspension): 충격 흡수 장치 원리

머신 바이스 (machine_vice): 공작 기계 고정 장치

판 스프링 (leaf_spring): 탄성체 역학

제트 엔진 (jet_engine): 항공 엔진 추진 원리

## 개발

### 빌드 ./gradlew build

### 테스트 ./gradlew test