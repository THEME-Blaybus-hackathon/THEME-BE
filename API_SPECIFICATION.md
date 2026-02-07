# API 명세서 (API Specification)

**프로젝트:** 3D 모델 기반 학습 플랫폼  
**버전:** v1.1  
**최종 업데이트:** 2025년 1월

---

## 목차
- [인증 및 OAuth](#인증-및-oauth)
- [3D 모델 메타데이터](#3d-모델-메타데이터)
- [개발 환경 전용 API](#개발-환경-전용-api)

---

## 인증 및 OAuth

### 1. OAuth 로그인 시작

소셜 로그인 제공자(Google, Kakao, Naver)의 인증 페이지로 리다이렉트합니다.

**Endpoint:** `GET /auth/{socialLoginType}`

**경로 변수:**
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `socialLoginType` | String | ✅ | 소셜 로그인 제공자 | `google`, `kakao`, `naver` |

**쿼리 파라미터:**
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `type` | String | ❌ | `web` | 요청 타입<br>- `web`: 세션 기반 (브라우저 리다이렉트)<br>- `api`: JWT 토큰 반환 |

**요청 예시:**
```http
GET /auth/google?type=api HTTP/1.1
Host: api.example.com
```

**응답:**
- HTTP 302 Redirect (소셜 로그인 제공자 인증 페이지로 이동)

**동작 흐름:**
1. 사용자 요청 시 `type` 파라미터를 세션에 저장
2. OAuth 제공자의 인증 URL 생성
3. 사용자를 인증 페이지로 리다이렉트

**로그 예시:**
```
🔐 OAuth login initiated: google (type: api)
↗️  Redirecting to: https://accounts.google.com/o/oauth2/auth?...
```

---

### 2. OAuth 콜백

소셜 로그인 제공자로부터 인증 코드를 받아 처리합니다.

**Endpoint:** `GET /auth/{socialLoginType}/callback`

**경로 변수:**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `socialLoginType` | String | ✅ | 소셜 로그인 제공자 |

**쿼리 파라미터:**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `code` | String | ✅ | OAuth 인증 코드 (제공자가 자동 전달) |

**요청 예시:**
```http
GET /auth/google/callback?code=4/0AY0e-g7... HTTP/1.1
Host: api.example.com
Cookie: JSESSIONID=ABC123...
```

**응답 (API 요청 - type=api):**
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "isNewUser": false
  }
}
```

**응답 (Web 요청 - type=web):**
- **신규 사용자:** HTTP 302 Redirect → `/oauth-signup` (추가 정보 입력 페이지)
- **기존 사용자:** HTTP 302 Redirect → `/dashboard` (세션 생성 완료)

**에러 응답 (API 요청):**
```json
{
  "success": false,
  "message": "액세스 토큰 획득 실패",
  "data": null
}
```

**에러 응답 (Web 요청):**
- HTTP 302 Redirect → `/login?error=oauth`

**동작 흐름:**
1. Authorization Code → Access Token 교환
2. Access Token → 사용자 정보 조회
3. DB에서 기존 사용자 확인
4. **신규 사용자:**
   - API 요청: 자동 회원가입 + JWT 반환
   - Web 요청: 추가 정보 입력 페이지로 이동
5. **기존 사용자:**
   - API 요청: JWT 토큰 반환
   - Web 요청: 세션 생성 + 대시보드 이동

**로그 예시:**
```
🔑 OAuth callback received from google
✅ Access token obtained
✅ User info obtained: user@example.com
✅ Existing user login: user@example.com
✅ JWT tokens issued (isNewUser: false)
```

**주요 개선 사항 (v1.1):**
- ✅ 코드 구조 개선: 메서드 분리 (`handleNewUser`, `handleExistingUser`, `respondWithJwt`, etc.)
- ✅ 상수 추출: `OAUTH_TYPE_SESSION_KEY`, `PENDING_USER_SESSION_KEY`, `OAUTH_TYPE_API`
- ✅ 로깅 강화: 이모지 + 단계별 상세 로그
- ✅ Swagger 문서화 추가

---

## 3D 모델 메타데이터

### 1. 3D 모델 메타데이터 조회

카테고리별 3D 모델 및 부품 정보를 조회합니다.

**Endpoint:** `GET /api/objects`

**쿼리 파라미터:**
| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `category` | String | ❌ | 필터링할 카테고리 | `drone`, `v4_engine`, `suspension` |

**요청 예시:**
```http
# 전체 조회
GET /api/objects HTTP/1.1

# 카테고리별 조회
GET /api/objects?category=drone HTTP/1.1
```

**응답 (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Piston",
    "description": "실린더 내부에서 왕복운동을 하며 연소 에너지를 기계적 에너지로 변환하는 부품",
    "imageUrl": "/images/v4_engine/v4_engine_image.jpg",
    "modelUrl": "/models/v4_engine/piston.glb",
    "category": "v4_engine"
  },
  {
    "id": 51,
    "name": "Propeller",
    "description": "모터의 회전력을 추력으로 변환하여 드론을 비행시키는 핵심 부품",
    "imageUrl": "/images/drone/drone_image.jpg",
    "modelUrl": "/models/drone/propeller.glb",
    "category": "drone"
  }
  // ... 더 많은 부품
]
```

**응답 필드 설명:**
| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 부품 고유 ID |
| `name` | String | 부품 이름 (영문) |
| `description` | String | 부품 설명 (한글) |
| `imageUrl` | String | 3D 모델 대표 이미지 경로 |
| `modelUrl` | String | 3D 모델 파일 경로 (.glb 또는 .gltf) |
| `category` | String | 모델 카테고리 |

**지원 카테고리:**
| 카테고리 | 설명 | 부품 수 |
|---------|------|---------|
| `v4_engine` | V4 엔진 | 8개 |
| `suspension` | 서스펜션 | 7개 |
| `robot_gripper` | 로봇 그리퍼 | 9개 |
| `robot_arm` | 로봇 팔 | 8개 |
| `machine_vice` | 기계 바이스 | 13개 |
| `leaf_spring` | 판 스프링 | 4개 |
| `drone` | 드론 | 12개 |

**동작 원리:**
1. 최초 호출 시 `object-metadata.json` 파일 로드
2. 메모리 캐싱 (`cachedObjects`)
3. `category` 파라미터가 있으면 필터링 후 반환
4. 없으면 전체 목록 반환

**캐싱 전략:**
- 애플리케이션 시작 후 첫 요청 시 JSON 파일 로드
- 이후 요청은 메모리 캐시 사용 (파일 I/O 없음)
- 서버 재시작 전까지 캐시 유지

**에러 처리:**
- JSON 파일 로드 실패 시 빈 배열 반환
- 로그: `❌ Failed to load object-metadata.json: {에러 메시지}`

**주요 개선 사항 (v1.1):**
- ✅ JSON 기반 리팩토링: 130줄 하드코딩 → 100줄 JSON 기반
- ✅ 메모리 캐싱 적용 (성능 최적화)
- ✅ 코드 라인 100줄 감소
- ✅ 7개 모델, 61개 부품 메타데이터 JSON 화
- ✅ Swagger 문서화 추가

**메타데이터 파일 위치:**
```
src/main/resources/object-metadata.json
```

**JSON 구조:**
```json
{
  "models": {
    "v4_engine": {
      "imagePath": "/images/v4_engine/v4_engine_image.jpg",
      "parts": [
        {
          "id": 1,
          "name": "Piston",
          "description": "...",
          "modelPath": "/models/v4_engine/piston.glb"
        }
      ]
    }
  }
}
```

---

## 개발 환경 전용 API

> ⚠️ **주의:** 이 섹션의 API는 `application.yml`의 `spring.profiles.active=dev`일 때만 활성화됩니다.  
> 운영 환경(`prod`)에서는 자동으로 비활성화됩니다.

### 1. 테스트용 오답 노트 생성

샘플 오답 노트 3개를 생성합니다. (개발/테스트 용도)

**Endpoint:** `POST /api/test-data/wrong-answers/mock`

**활성화 조건:**
```yaml
spring:
  profiles:
    active: dev  # 개발 환경에서만 동작
```

**요청 예시:**
```http
POST /api/test-data/wrong-answers/mock HTTP/1.1
Host: localhost:8080
```

**응답 (200 OK):**
```
Mock wrong answer notes created successfully!
```

**생성되는 샘플 데이터:**
```json
[
  {
    "userId": "test_user_mock",
    "quizId": "mock_quiz_001",
    "questionId": "q1",
    "question": "크랭크축은 왕복운동을 회전운동으로 변환한다.",
    "userAnswer": "X",
    "correctAnswer": "O",
    "explanation": "크랭크축은 피스톤의 왕복운동을 받아 회전운동으로 변환하는 핵심 부품입니다.",
    "category": "동력전달",
    "objectName": "v4_engine",
    "reviewed": false
  }
  // ... 2개 더
]
```

**로그 예시:**
```
⚠️ Creating mock wrong answer notes - DEV ENVIRONMENT ONLY
✅ Mock data created: 3 entries
```

**주요 개선 사항 (v1.1):**
- ✅ `@Profile("dev")` 어노테이션 추가
- ✅ Swagger 태그: "Test Data API (DEV ONLY)"
- ✅ 로깅 레벨 변경: `log.info` → `log.warn`
- ✅ 운영 환경에서 자동 비활성화

---

## 부록

### A. HTTP 상태 코드

| 상태 코드 | 설명 |
|-----------|------|
| 200 | 성공 |
| 302 | 리다이렉트 (OAuth 플로우) |
| 400 | 잘못된 요청 (OAuth 실패) |
| 401 | 인증 실패 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

### B. 인증 방식

**API 요청:**
```http
Authorization: Bearer {JWT_ACCESS_TOKEN}
```

**Web 요청:**
- 세션 기반 인증 (Cookie: `JSESSIONID`)

### C. 환경별 설정

**개발 환경 (`dev`):**
- H2 인메모리 DB
- 테스트 데이터 API 활성화
- Swagger UI 활성화

**운영 환경 (`prod`):**
- 외부 DB (MySQL/PostgreSQL)
- 테스트 데이터 API 비활성화
- Swagger UI 비활성화 권장

### D. Swagger UI 접근

**URL:** `http://localhost:8080/swagger-ui/index.html`

모든 API를 대화형으로 테스트할 수 있습니다.

---

## 변경 이력

### v1.1 (2025-01-XX)
- ✅ OAuth API 구조 개선 (메서드 분리, 상수 추출, 로깅 강화)
- ✅ 3D 모델 메타데이터 API 완전 리팩토링 (JSON 기반 + 캐싱)
- ✅ 테스트 데이터 API 프로파일 적용 (`@Profile("dev")`)
- ✅ 불필요한 API 제거 (학습 진도, 3D 모델별 오답 조회)
- ✅ Swagger 문서화 강화

### v1.0 (초기 버전)
- 기본 OAuth 로그인 구현
- 3D 모델 메타데이터 하드코딩
- 테스트 데이터 API (프로파일 미적용)

---

**문서 작성자:** AI Assistant  
**문서 버전:** 1.1  
**마지막 검증일:** 2025-01-XX
