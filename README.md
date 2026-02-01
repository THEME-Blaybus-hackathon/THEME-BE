@@ THE:ME Backend

3D 기반 공학 교육 플랫폼 백엔드 API

@@ 주요 기능

- JWT 기반 사용자 인증
- 소셜 로그인 (Google, Kakao, Naver)
- AI 어시스턴트 (GPT-5-mini)
- **3D 모델 에셋 스트리밍 및 메타데이터 API 제공** (New)

@@ 기술 스택

- Java 17
- Spring Boot 3.4.2
- Spring Security
- H2 Database
- OpenAI API

@@ 시작하기

@@@ 요구사항

- JDK 17 이상
- Gradle

@@@ 설치

git clone https://github.com/YOUR_USERNAME/SIMVEX-Backend.git
cd SIMVEX-Backend

@@@ 설정

1. `application.properties.example` 복사 (필수)
   **보안을 위해 실제 키는 포함되어 있지 않습니다. 복사 후 값을 채워주세요.**

cp src/main/resources/application.properties.example src/main/resources/application.properties


2. API 키 설정 (`application.properties`)

# OpenAI (GPT-5-mini)
openai.api.key=your-key-here

# JWT Secret Key
jwt.secret=your-secret-key

# Google OAuth (선택)
sns.google.client.id=your-id
sns.google.client.secret=your-secret

# Kakao OAuth (선택)
sns.kakao.client.id=your-key

# Naver OAuth (선택)
sns.naver.client.id=your-id
sns.naver.client.secret=your-secret


@@@ 실행

./gradlew bootRun

서버: http://localhost:8080

@@ API 문서

Swagger UI: http://localhost:8080/swagger-ui.html

@@@ 주요 엔드포인트

**인증**
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `GET /auth/{provider}` - OAuth 로그인

**3D 모델 & 에셋 (New)**
- `GET /api/objects?category={keyword}` - 모델 부품 리스트 및 메타데이터 조회
- `GET /asset/{category}/{filename}` - 3D 파일(.glb) 및 이미지 직접 접근 (로그인 불필요)

**AI 어시스턴트**
- `POST /api/ai/ask` - 질문하기

{
  "objectName": "drone",
  "question": "프로펠러의 역할은?",
  "sessionId": "user-123",
  "selectedPart": "impeller_blade"
}

@@ 지원 3D 모델 (API 키워드)

API 요청 시 `category` 파라미터에 아래 **소문자 키워드**를 사용하세요.

| 모델명 | API 키워드 (`category`) |
| 로봇 팔 | `robot_arm` |
| 머신 바이스 | `machine_vice` |
| 판 스프링 | `leaf_spring` |
| 드론 | `drone` |
| V4 엔진 | `v4_engine` |
| 로봇 집게 | `robot_gripper` |
| 서스펜션 | `suspension` |

@@ 개발

@@@ 빌드

./gradlew build

@@@ 테스트

./gradlew test