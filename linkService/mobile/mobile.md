## 모바일공무원인증 연계 API

### 개요

- 브라우저에 민간SaaS 로그인이 되어 있으면, 다른 민간 SaaS에서는 로그인이 불필요합니다. (단, 동일 브라우저 내 탭에서만 가능)
- 이용기관에서 적용하는 정책(지원하는 디바이스, 지원하는 브라우저 등) 에 따라서 모바일 로그인 기능 App To App 이나 Web To App 을 적용할 수 있습니다.
- 개발용 서버: `https://www.saas.go.kr/auth-stg`
    - 테스트 공무원증이 있거나 개발 시 사용
- 운영용 서버: `https://www.saas.go.kr/auth`
    - 테스트 공무원증 사용 불가

### API 목록

| 요청 URL                   | 메서드  | 응답 형식 | 설명                                             |
|--------------------------|------|-------|------------------------------------------------|
| `/oauth2/authorize`      | GET  | JSON  | 연계 API에게 권한 인증을 요청하는 API                       |
| `/oauth2/token`          | POST | JSON  | 연계 API에 Access Token / Refresh Token을 요청하는 API |
| `/oauth2/userinfo`       | GET  | JSON  | 로그인한 사용자 정보를 요청하는 API                          |
| `/oauth2/connect/logout` | GET  | JSON  | 로그인한 사용자의 세션을 만료시키는 API                        |
| `/mobile/authorize`      | POST | JSON  | App to App 권한 인증을 요청하는 API                     |
| `/mobile/txCheck`        | POST | JSON  | App to App 검증 완료 여부를 확인하는 API                  |
| `/login`                 | POST | JSON  | App to App 검증 후 로그인을 요청하는 API                  |

***[에러코드](#에러-코드)는 문서 최하단에 있습니다.***

---

### API 명세

### (1) 로그인

#### 가. 인가 코드 요청

- 요청 방식: `GET`
- 요청 URI: `/oauth2/authorize`

##### 요청 파라미터

| 항목                    | 타입      | 설명               | 비고(예시)                                             |
|-----------------------|---------|------------------|----------------------------------------------------|
| response_type         | string  | code             | 고정값                                                |
| scope                 | string  | openid           | 고정값                                                |
| client_id             | string  | 지정한 client_id    | 연계서비스 ID (ex: `LKSV2099010119000196`)              |
| redirect_uri          | string  | 지정한 redirect_uri | ex) `http://testsaas.com/oauth/callback`           |
| state                 | string  | 랜덤 문자열           | -                                                  |
| nonce                 | string  | 랜덤 문자열           | -                                                  |
| web_login             | boolean | true / false     | 기본 로그인의 경우 `true`, <br> Wep to App 로그인의 경우 `false` |
| code_challenge        | string  | 랜덤 문자열           | `code_verifier`의 해시 값 (Wep to App 사용 시)            |
| code_challenge_method | string  | S256             | 고정값 (Wep to App 사용 시)                              |

`redirect_uri`는 통합관리포털에서 이용 신청 시 입력하며, 이후 운영 메뉴(`운영관리 > 연계서비스`)에서 수정 및 추가할 수 있습니다.

##### 요청 URI 예시

```text
https://www.saas.go.kr/auth/oauth2/authorize?
    response_type=code&
    scope=openid&
    client_id=연계서비스ID&
    redirect_uri=지정한 redirect_uri&
    state=랜덤 문자열&
    nonce=랜덤 문자열&
    web_login=true&                 *App to App 사용 시 false
    code_challenge=랜덤 문자열&     *App to App 사용 시
    code_challenge_method=S256      *App to App 사용 시
```

##### 응답 Redirection URI 예시

```text
https://지정한 redirect_uri?code=코드값&state=요청 시 전달한 값
```

- 프로세스

    1) 로그인이 되어 있지 않았으면 로그인 페이지로 이동합니다.
    2) 로그인 페이지에서 QR 인증을 하여 로그인이 성공하면 redirect_uri 페이지로 이동합니다.
    3) 로그인이 되어 있는 경우 즉시 redirect_uri 페이지로 이동합니다.

##### 사후 처리

- Redirection URI에서 취득한 Authorization Code를 사용하여 Token을 발급합니다.

#### 나. Token 요청

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

##### 요청 헤더

| 항목           | 설명                                  | 비고(예시) |
|--------------|-------------------------------------|--------|
| Content-Type | `application/x-www-form-urlencoded` | 고정값    |

##### 요청 본문

| 항목            | 타입     | 설명                   | 비고(예시)                                   |
|---------------|--------|----------------------|------------------------------------------|
| grant_type    | string | `authorization_code` | 고정값                                      |
| client_id     | string | 지정한 client_id        | 연계서비스 ID (ex: `LKSV2099010119000196`)    |
| client_secret | string | 지정한 client_secret    | API 키                                    |
| redirect_uri  | string | 지정한 redirect_uri     | ex) `http://testsaas.com/oauth/callback` |
| code          | string | 발급 받은 code           | 토큰 연장 할 땐 사용 안 함                         |
| code_verifier | string | 랜덤 문자열               | App to App 요청 시 사용                       |

##### 응답 항목

| 항목            | 타입      | 설명                   | 비고(예시) |
|---------------|---------|----------------------|--------|
| access_token  | string  | API 요청 인증에 사용        | -      |
| refresh_token | string  | Access Token 갱신에 사용  | -      |
| id_token      | string  | 로그아웃 시 사용            | -      |
| scope         | string  | `openid`             | 고정값    |
| token_type    | string  | `Bearer`             | 고정값    |
| expires_in    | Integer | Access Token 유효기간(초) | `3000` |

##### 사후 처리

1) 응답받은 `access_token`, `refresh_token`, `id_token` 값을 Cookie, Local Storage 등에 저장합니다.
2) `access_token`을 사용하여 로그인한 사용자 정보를 조회합니다.

#### 다. 사용자 정보 조회

- 요청 방식: `GET`
- 요청 URI: `/oauth2/userinfo`

##### 요청 헤더

| 항목            | 설명                       | 비고(예시) |
|---------------|--------------------------|--------|
| Authorization | `Bearer ${ACCESS_TOKEN}` | -      |

##### 응답 항목

| 항목       | 타입     | 설명           | 비고(예시) |
|----------|--------|--------------|--------|
| cn       | string | 사용자 식별값      | -      |
| name     | string | 사용자 이름       | -      |
| instCode | string | 사용자가 속한 부서코드 | -      |

---

### (2) Token 갱신

인증을 위한 Access Token의 유효기간은 1시간으로, Access Token이 만료되면 갱신요청을 하여 Token을 갱신할 수 있습니다.

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

##### 요청 헤더

| 항목           | 설명                                  | 비고(예시) |
|--------------|-------------------------------------|--------|
| Content-Type | `application/x-www-form-urlencoded` | -      |

##### 요청 본문

| 항목            | 타입     | 설명                | 비고(예시)                                 |
|---------------|--------|-------------------|----------------------------------------|
| grant_type    | string | `refresh_token`   | 고정값                                    |
| client_id     | string | 지정한 client_id     | 연계서비스 ID <br> ex) LKSV2099010119000196 |
| client_secret | string | 지정한 client_secret | API 키                                  |
| refresh_token | string | 저장한 refresh_token | -                                      |

##### 응답 항목

| 항목            | 타입      | 설명                          | 비고(예시) |
|---------------|---------|-----------------------------|--------|
| access_token  | string  | API 요청 인증에 사용               | -      |
| refresh_token | string  | Access Token 갱신에 사용         | -      |
| id_token      | string  | 로그아웃 시 사용                   | -      |
| scope         | string  | `openid`로 고정                | 고정값    |
| token_type    | string  | `Bearer`로 고정                | 고정값    |
| expires_in    | Integer | 3000 (Access Token 유효기간(s)) | -      |

##### 사후 처리

- 갱신받은 `access_token`, `refresh_token`, `id_token` 값을 Cookie, Local Storage 등에 저장합니다.

---

### (3) 로그아웃

- 요청 방식: `GET`
- 요청 URI: `/oauth2/connect/logout`

##### 요청 파라미터

| 항목                       | 타입     | 설명               | 비고(예시)                   |
|--------------------------|--------|------------------|--------------------------|
| id_token_hint            | string | 저장된 id_token     | -                        |
| post_logout_redirect_uri | string | 지정한 redirect_uri | 예: `http://testsaas.com` |

##### 사후 처리

- 로그아웃에 성공하면 지정한 post_logout_redirect_uri 페이지로 이동되며, 저장된 access_token, refresh_token, id_token을 삭제합니다.

---

### (4) App to App

- 모바일 공무원증 App to App 가이드는 Mobile Web Browser에서 모바일 공무원증 Application 호출하는 방법만 안내합니다.
- CSS, javascript, Angular, React, Vue.js 등의 browser에서 발생하는 오류는 기술 지원이 어려우므로 양해 부탁드립니다.
- App to App 흐름은 모바일 앱이 WebView 없이 BMC 앱을 직접 실행하고, 인증 완료 후 authorization code 를 받아 토큰 발급 단계로 연결하는 방식입니다.
- 실제 연동 시 핵심 순서는 아래와 같습니다.

### 연동 흐름

> 1. PKCE 값 생성
>  2. `/auth/mobile/authorize` 호출
>  3. 응답에서 `nonce`, `qrData`, 세션 쿠키 저장
>  4. `qrData` 로 BMC 앱 실행 (모바일공무원증앱 호출)
>  5. `/auth/mobile/txCheck` 를 반복 호출해서 `COMPLETE` 확인
>  6. `/auth/login` 호출 시 `username=nonce`, `password=nonce` 사용
>  7. `/auth/login` 응답의 JSON 에서 `code` 추출
>  8. `/auth/oauth2/token` 호출 시 `code`, 원래의 `code_verifier`, 동일한 `redirect_uri`, 클라이언트 인증 정보 사용

### API 운영 환경

- 개발/스테이징: `https://www.saas.go.kr/auth-stg`
- 운영: `https://www.saas.go.kr/auth`

### API 목록

| 요청 URL              | 메서드  | 응답 형식 | 설명                                                   |
|---------------------|------|-------|------------------------------------------------------|
| `/mobile/authorize` | POST | JSON  | App to App 시작 시 `nonce`, `qrData`, 세션을 생성하는 API      |
| `/mobile/txCheck`   | POST | JSON  | BMC 인증 완료 여부를 확인하는 API                               |
| `/login`            | POST | JSON  | App to App 인증 후 authorization code 를 JSON으로 반환하는 API |
| `/oauth2/token`     | POST | JSON  | authorization code 로 토큰 발급을 요청하는 API                 |

### API 명세

####          * PKCE 값 생성

- 앱은 시작 전에 아래 값을 먼저 생성해야 합니다.

| 항목                    | 타입     | 설명                                 | 비고(예시)                        |
|-----------------------|--------|------------------------------------|-------------------------------|
| code_verifier         | string | PKCE 원본 값                          | 이후 `txCheck`, `token` 단계까지 유지 |
| code_challenge        | string | `BASE64URL(SHA256(code_verifier))` | `authorize` 요청 시 사용           |
| code_challenge_method | string | `S256`                             | 고정값                           |
| state                 | string | 랜덤 문자열                             | -                             |

#### (1) 인가 요청

- 요청 방식: `POST`
- 요청 URI: `/mobile/authorize`

##### 요청 파라미터

| 항목                    | 타입     | 설명                  | 비고(예시) |
|-----------------------|--------|---------------------|--------|
| response_type         | string | `code`              | 고정값    |
| scope                 | string | `openid`            | 고정값    |
| client_id             | string | 등록된 OAuth2 클라이언트 ID | 필수     |
| redirect_uri          | string | 등록된 redirect URI    | 필수     |
| code_challenge        | string | PKCE code challenge | 필수     |
| code_challenge_method | string | `S256`              | 고정값    |
| state                 | string | 랜덤 문자열              | -      |

##### 요청 예시

```http
POST /auth/mobile/authorize
Content-Type: application/x-www-form-urlencoded

response_type=code
&client_id=your-client-id
&redirect_uri=myapp://callback
&code_challenge=E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
&code_challenge_method=S256
&state=abc123
```

##### 응답 항목

| 항목                 | 타입     | 설명                                 | 비고(예시)        |
|--------------------|--------|------------------------------------|---------------|
| nonce              | string | 이후 로그인 요청에 사용하는 값                  | 필수 저장         |
| qrData.spDid       | string | BMC 실행용 SP DID                     | -             |
| qrData.serviceCode | string | BMC 실행용 서비스 코드(발급 받은 Service Code) | -             |
| qrData.nonce       | string | 서버가 생성한 nonce                      | 서버 응답값 그대로 사용 |
| qrData.callBackUrl | string | BMC 검증 callback URL                | 서버 응답값 그대로 사용 |
| qrData.encryptType | string | 암호화 타입                             | 고정값: 2        |

##### 응답 예시

```json
{
  "nonce": "988cef69d625e4db508b6b78151cabf5...",
  "qrData": {
    "spDid": "did:omn:example",
    "serviceCode": "bmc.service",
    "nonce": "988cef69d625e4db508b6b78151cabf5...",
    "callBackUrl": "환경별 서버 설정값",
    "encryptType": 2
  }
}
```

##### 후속 처리

1. 응답에서 `nonce`, `qrData`, 세션 쿠키를 저장합니다.
2. 최초에 생성한 `code_verifier`, 요청에 사용한 `redirect_uri` 도 함께 유지합니다.
3. 서버가 준 `qrData` 로 BMC 앱을 실행합니다.

##### 세션 쿠키 참고

| 환경  | Context Path | Cookie Name       | Cookie Path |
|-----|--------------|-------------------|-------------|
| stg | `/auth-stg`  | `mgsaas_auth_stg` | `/auth-stg` |
| prd | `/auth`      | `mgsaas_auth`     | `/`         |

---

#### (2) BMC 실행

- 앱은 `/mobile/authorize` 응답의 `qrData` 를 그대로 사용해 BMC 앱을 실행합니다.

```curl 
bmc://verify_vp?
  appName={자사서비스앱이름}&
  type=VERIFY&
  spDid={응답받은 spDid}&
  serviceCode={응답받은 서비스코드}&
  callBackUrl={지정한 callback URL}&
  nonce={응답받은 nonce값}&
  encryptType=2
```

#### (3) 검증 요청

- 요청 방식: `POST`
- 요청 URI: `/mobile/txCheck`

##### 요청 파라미터

| 항목           | 타입     | 설명                          | 비고(예시) |
|--------------|--------|-----------------------------|--------|
| codeVerifier | string | 최초 생성한 `code_verifier` 원본 값 | 필수     |

##### 요청 예시

```http
POST /auth/mobile/txCheck
Content-Type: application/x-www-form-urlencoded
Cookie: mgsaas_auth_stg=...

codeVerifier=dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
```

##### 응답 항목

| 항목             | 타입      | 설명       | 비고(예시)                                    |
|----------------|---------|----------|-------------------------------------------|
| result         | boolean | 처리 결과    | -                                         |
| txCompleteCode | string  | 인증 상태 코드 | `WAITING`, `COMPLETE`, `TIMEOUT`, `ERROR` |

##### 응답 예시

```json
{
  "result": true,
  "txCompleteCode": "WAITING"
}
```

##### 후속 처리

- `WAITING`: 계속 폴링
- `COMPLETE`: 다음 `/login` 진행
- `TIMEOUT`: 실패 처리
- `ERROR`: 실패 처리

##### 참고

- `/mobile/authorize` 응답에서 받은 세션 쿠키를 그대로 보내야 합니다.
- `codeVerifier` 는 최초 PKCE 생성 시의 원본값과 정확히 같아야 합니다.

---

#### (4) 인가 코드 (authorization code) 발급

- 요청 방식: `POST`
- 요청 URI: `/login`

##### 요청 파라미터

| 항목       | 타입     | 설명      | 비고(예시)                     |
|----------|--------|---------|----------------------------|
| username | string | `nonce` | `/mobile/authorize` 응답값 사용 |
| password | string | `nonce` | `/mobile/authorize` 응답값 사용 |

##### 요청 예시

```http
POST /auth/login
Content-Type: application/x-www-form-urlencoded
Cookie: mgsaas_auth_stg=...

username=988cef69d625e4db508b6b78151cabf5...
&password=988cef69d625e4db508b6b78151cabf5...
```

##### 응답 항목

| 항목   | 타입     | 설명                 | 비고(예시)           |
|------|--------|--------------------|------------------|
| code | string | authorization code | 이후 토큰 발급 단계에서 사용 |

##### 응답 예시

```json
{
  "code": "authorization_code_value"
}
```

##### 후속 처리

1. `/login` 응답의 JSON 에서 `code` 값을 추출합니다.
2. 이 값을 저장한 뒤 `/oauth2/token` 단계에 사용합니다.

##### 참고

- 여기서 사용하는 `nonce` 는 `/mobile/authorize` 응답에서 받은 값입니다.
- 요청시 세션 쿠키를 같이 보내야 합니다.

---

##### (5) 토큰 발급

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

##### 요청 본문

| 항목            | 타입     | 설명                          | 비고(예시)                                   |
|---------------|--------|-----------------------------|------------------------------------------|
| grant_type    | string | `authorization_code`        | 고정값                                      |
| client_id     | string | 지정한 client_id               | 연계서비스 ID (ex: `LKSV2099010119000196`)    |
| client_secret | string | 지정한 client_secret           | API 키                                    |
| redirect_uri  | string | 지정한 redirect_uri            | ex) `http://testsaas.com/oauth/callback` |
| code          | string | 발급 받은 code                  | 토큰 연장 할 땐 사용 안 함                         |
| code_verifier | string | 최초 생성한 `code_verifier` 원본 값 | 필수                                       |

##### 응답 항목

| 항목            | 타입      | 설명                   | 비고(예시) |
|---------------|---------|----------------------|--------|
| access_token  | string  | API 요청 인증에 사용        | -      |
| refresh_token | string  | Access Token 갱신에 사용  | -      |
| id_token      | string  | 로그아웃 시 사용            | -      |
| scope         | string  | `openid`             | 고정값    |
| token_type    | string  | `Bearer`             | 고정값    |
| expires_in    | Integer | Access Token 유효기간(초) | `3600` |

---

### 에러 코드 / 확인 포인트

##### `/login` 에서 `code` 가 안 나올 때

- 세션 쿠키가 계속 유지되고 있는지 확인합니다.
- `username`, `password` 에 `nonce` 를 넣었는지 확인합니다.
- `nonce` 가 `/mobile/authorize` 응답값과 동일한지 확인합니다.
- `txCheck` 완료 후 호출했는지 확인합니다.

##### 토큰 발급 단계에서 오류가 날 때

- `/login` 응답의 `code` 를 사용했는지 확인합니다.
- 최초 생성한 `code_verifier` 와 동일한 값을 사용했는지 확인합니다.
- `redirect_uri` 가 authorize 요청 때와 동일한지 확인합니다.
- client 인증 정보가 포함되어 있는지 확인합니다.

##### 사전 확인

- client_id, client_secret, redirect_uri 는 등록된 값이어야 합니다.
- 잘못된 client_id, client_secret, redirect_uri 로 요청하면 정상 동작하지 않습니다.
- App to App 흐름에서는 authorize 단계의 세션 쿠키, nonce, code_verifier 연결이 끊기지 않는 것이 중요합니다.

---

## 에러 코드

| 코드      | 에러 메시지                    | 상태코드 |
|---------|---------------------------|------|
| MP10001 | 시스템 장애로 통신이 원활하지 않습니다     | 500  |
| MP10002 | 잘못된 요청입니다                 | 400  |
| MP10003 | 요청하신 데이터가 없습니다            | 404  |
| MP10004 | 등록된 Client ID가 없습니다       | 404  |
| MP10005 | 등록된 ApiKey가 없습니다          | 404  |
| MP10006 | 등록된 서비스가 없습니다             | 404  |
| MP10007 | 지원하지 않는 형식입니다             | 405  |
| MP20001 | 인증되지 않았습니다                | 401  |
| MP20002 | 접근 권한이 없습니다               | 403  |
| MP20003 | 요청에 필요한 항목이나 값이 잘못되었습니다   | 400  |
| MP20004 | OAuth2 인증 흐름을 통한 접근이 아닙니다 | 401  |
| MP30001 | 잘못된 토큰 형식입니다              | 400  |
| MP30002 | 유효하지 않은 토큰입니다             | 401  |
| MP30003 | 유효하지 않은 API키입니다           | 401  |
| MP30004 | 유효하지 않은 접근입니다             | 401  |
| MP30005 | 만료된 인증서 요청입니다             | 401  |
