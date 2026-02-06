# THE:ME Backend

**3D 기반 공학 교육 플랫폼 THE:ME의 백엔드 API 서버**  
사용자는 3D 모델을 탐색하며 AI 어시스턴트와 실시간으로 대화하고, 학습 내용을 메모하며, AI가 생성한 퀴즈를 풀고, 모든 학습 과정을 PDF 리포트로 저장할 수 있습니다.

---

## 🚀 주요 기능

### 1. 🔐 JWT & OAuth 인증
- **세션 기반 Web 로그인** 및 **JWT 기반 API 로그인** 통합 지원
- **소셜 로그인**: Google, Kakao, Naver OAuth 2.0 연동
- Access Token & Refresh Token 자동 발급 및 갱신

### 2. 🤖 AI 어시스턴트 (GPT-4o-mini)
- **3D 엔지니어링 모델 특화** 대화형 AI
- **대화 맥락 인식 강화**: "두 개", "그거", "이거" 같은 대명사 자동 해석
- 7개 3D 모델별 특화 프롬프트 적용
- 대화 히스토리 기반 컨텍스트 유지

### 3. 🎯 AI 맞춤형 퀴즈
- **대화 내용 기반 OX 퀴즈 자동 생성**
- 채점 결과 및 해설 제공
- 오답 노트 자동 저장 및 복습 관리

### 4. 📄 AI 스마트 학습 리포트
- **대화 요약 + 학습 메모 + 퀴즈 오답 노트** 통합 PDF 생성
- 한글 폰트 완벽 지원 (HYGoThic)
- 사용자별 맞춤 학습 이력 리포트

### 5. 📦 3D 모델 에셋 스트리밍
- 7개 엔지니어링 3D 모델 (.glb) 제공
- 부품별 메타데이터 및 이미지 API

### 6. 📝 학습 메모 & 오답 노트
- 부품별 학습 메모 저장 및 조회
- 오답 노트 복습 여부 추적
- PDF 리포트 자동 반영

---

## 🛠️ 기술 스택

### Backend Framework
- **Java 17** / **Spring Boot 3.4.2**
- **Spring Security 7.0.2** (BCrypt, Role Management)
- **Spring Data JPA** + **H2 Database**

### 인증 & 보안
- **JWT** (JJWT 0.12.3): Access/Refresh Token
- **OAuth 2.0**: Google, Kakao, Naver

### AI & Document Processing
- **OpenAI API**: GPT-4o-mini (대화), GPT-4o (요약, 퀴즈 생성)
- **iText PDF 5.5.13** + **iText Asian**: PDF 생성 및 한글 렌더링

### API 문서화
- **SpringDoc OpenAPI 2.3.0** (Swagger UI)

---

## 📡 API 엔드포인트

### 🔐 인증 (Authentication)
```
POST   /api/auth/login              JWT 로그인 (이메일/비밀번호)
POST   /api/auth/signup             회원가입
POST   /api/auth/refresh            토큰 갱신
GET    /auth/{provider}             OAuth 소셜 로그인 (Google/Kakao/Naver)
GET    /auth/{provider}/callback    OAuth 콜백
GET    /api/user/me                 내 정보 조회
```

### 🤖 AI 어시스턴트
```
POST   /api/ai/ask                  AI에게 질문하기
POST   /api/ai/summary              대화 내용 AI 요약 (텍스트)
POST   /api/ai/report               학습 리포트 PDF 다운로드
DELETE /api/ai/session              대화 세션 초기화
DELETE /api/ai/history              대화 기록 삭제
```

### 🎯 퀴즈 (Quiz)
```
POST   /api/quiz/generate-from-chat 대화 기반 OX 퀴즈 생성
POST   /api/quiz/submit             퀴즈 답안 제출 & 채점
```

### 📝 오답 노트 (Wrong Answer Note)
```
GET    /api/wrong-answers           오답 노트 전체 조회
GET    /api/wrong-answers/unreviewed 미복습 오답 조회
PUT    /api/wrong-answers/{noteId}/review 복습 완료 처리
DELETE /api/wrong-answers/{noteId}  오답 노트 삭제
```

### 📦 3D 모델 & 에셋
```
GET    /api/objects                 3D 모델 메타데이터 조회
GET    /asset/{category}/{filename} 3D 모델(.glb) 및 이미지 스트리밍
```

### 📋 메모 (Memo)
```
GET    /api/memo                    메모 전체 조회
POST   /api/memo                    메모 생성
PUT    /api/memo/{memoId}           메모 수정
DELETE /api/memo/{memoId}           메모 삭제
```

> 📖 **상세 API 명세서**: [API_SPECIFICATION.md](./API_SPECIFICATION.md) 참고

---

## 📱 OAuth 소셜 로그인 가이드

### 웹 클라이언트 (세션 방식)
```javascript
// 소셜 로그인 페이지로 이동
window.location.href = '/auth/google'; // or '/auth/kakao', '/auth/naver'
// 로그인 성공 시 자동으로 대시보드로 리다이렉트
```

### API 클라이언트 (JWT 방식) - 향후 지원 예정
```javascript
// Popup 방식 (예정)
const popup = window.open('/auth/google', 'OAuth Login', 'width=500,height=600');
window.addEventListener('message', (event) => {
  if (event.data.accessToken) {
    localStorage.setItem('accessToken', event.data.accessToken);
    localStorage.setItem('refreshToken', event.data.refreshToken);
  }
});
```

> 📖 **OAuth 상세 가이드**: [OAUTH_GUIDE.md](./OAUTH_GUIDE.md) 참고

---

## 🚀 시작하기

### 📋 요구사항
- **JDK 17** 이상
- **Gradle 7.x** 이상

### 📥 설치
```bash
git clone https://github.com/YOUR_USERNAME/THE-ME-Backend.git
cd THE-ME-Backend
```

### ⚙️ 설정 (`src/main/resources/application.properties`)
```properties
# OpenAI API Key
openai.api.key=your-openai-api-key

# JWT Secret
jwt.secret=your-jwt-secret-key-min-256bits

# OAuth (선택사항)
sns.google.client.id=your-google-client-id
sns.google.client.secret=your-google-secret

sns.kakao.client.id=your-kakao-rest-api-key
sns.kakao.client.secret=your-kakao-secret

sns.naver.client.id=your-naver-client-id
sns.naver.client.secret=your-naver-secret
```

### ▶️ 실행
```bash
./gradlew bootRun
```

**서버 주소**: `http://localhost:8080`  
**Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🎓 지원 3D 모델

| 모델 키워드 | 설명 | 학습 내용 |
|------------|------|----------|
| `drone` | 드론 | 프로펠러, 배터리, 비행 원리 |
| `robot_arm` | 로봇 팔 | 관절 구조, 작동 메커니즘 |
| `robot_gripper` | 로봇 집게 | 그리퍼 작동 원리 |
| `v4_engine` | V4 엔진 | 내연기관 구조, 연소 사이클 |
| `suspension` | 서스펜션 | 충격 흡수 장치, 스프링 원리 |
| `machine_vice` | 머신 바이스 | 공작 기계 고정 장치 |
| `leaf_spring` | 판 스프링 | 탄성체 역학, 변형 원리 |

---

## 🏗️ 프로젝트 구조

```
src/main/java/com/example/Project/
├── controller/
│   ├── api/                      # REST API 컨트롤러
│   │   ├── AiAssistantController.java
│   │   ├── AuthApiController.java
│   │   ├── QuizController.java
│   │   ├── WrongAnswerNoteController.java
│   │   └── UserController.java
│   ├── OauthController.java      # OAuth 로그인
│   ├── AiSummaryController.java  # AI 요약
│   ├── MemoController.java       # 학습 메모
│   └── LearningObjectController.java
├── service/
│   ├── AiAssistantService.java
│   ├── QuizService.java
│   ├── PdfExportService.java
│   └── ...
├── repository/                   # JPA 리포지토리
├── security/                     # JWT & Security
└── dto/                          # DTO 클래스

src/main/resources/
├── prompts.json                  # AI 모델별 프롬프트
├── object-metadata.json          # 3D 모델 메타데이터
└── assets/                       # 3D 모델 파일
```

---

## 🧪 개발 & 빌드

### 빌드
```bash
./gradlew build
```

### 테스트
```bash
./gradlew test
```

### 로그 확인
```bash
tail -f logs/application.log
```

---

## 📜 라이선스

이 프로젝트는 교육 목적으로 개발되었습니다.

---

## 📞 문의

프로젝트 관련 문의는 GitHub Issues를 통해 남겨주세요.