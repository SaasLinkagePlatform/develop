## EPKI 연계 SaaS

### 개요

EPKI 연계 SaaS는 교육부 산하 기관, 전국 시도 교육청, 학교 등 교육·연구기관 종사자들이 민간 SaaS에 로그인 시,
EPKI 인증서를 통해 로그인을 할 수 있도록 인증서 검증을 연계하는 서비스입니다.
EPKI 연계 로그인은 OAuth 2.0 프로토콜을 통해 서비스되고 있습니다.

- 개발 서버: <https://saas.go.kr/epki-stg>
- 운영 서버: <https://saas.go.kr/epki>
- 테스트 인증서가 따로 존재하지 않기 때문에, 개발 서버에서는 아래 두 가지 방식의 로그인을 제공합니다.
    - 인증서 없는 로그인 : 인증서 검증을 건너뛰고 임의의 사용자 정보로 로그인 수행
    - 실인증서 로그인 : 실제 인증서를 첨부하고, 해당 인증서의 사용자 정보로 로그인 수행

### API 목록

| 요청 URL              | 메서드  | 응답 형식        | 설명                                            |
|---------------------|------|--------------|-----------------------------------------------|
| `/oauth2/authorize` | GET  | Redirect URI | 연계 SaaS에 인가 코드를 요청하는 API                      |
| `/oauth2/token`     | POST | JSON         | 연계 SaaS에 Access Token/Refresh Token을 요청하는 API |
| `/userinfo`         | GET  | JSON         | 로그인 한 사용자의 정보를 요청하는 API                       |
| `/connect/logout`   | GET  | Redirect URI | 로그인 한 사용자의 세션을 만료시키는 API                      |

### 주요 파라미터 설명

```text
- client_id, client_secret은 이용신청 승인 이후 통합관리포털에서 발급되며, [운영관리] > [연계서비스] 에서 확인 가능합니다.

- client_id: 연계서비스ID

- client_secret: API Key

- redirect_uri, post_logout_redirect_uri은 통합관리포털에서 이용 신청 시 입력합니다.

- redirect_uri : EPKI 연계 SaaS에게 인가 코드를 요청하고 리다이렉트되는 민간 SaaS의 서비스 URI

- post_logout_redirect_uri : 로그아웃 후 리다이렉트되는 민간 SaaS의 서비스 URI
```

***[에러코드](#에러-코드)는 문서 최하단에 있습니다.**

---

### (1) 로그인

#### 가. 인가 코드 요청

- 요청 방식: `GET`
- 요청 URI: `/oauth2/authorize`

#### 요청

쿼리 파라미터:

| 항목            | 타입     | 설명                                                                   | 비고(예시)                                 |
|---------------|--------|----------------------------------------------------------------------|----------------------------------------|
| client_id     | String | 연계 서비스 ID<br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196               |
| redirect_uri  | String | 인가 코드 수신 URI<br/> - 서비스 신청 시 입력한 redirect_uri<br/>                   | ex) http://testsaas.com/oauth/callback |
| response_type | String | `code` 고정                                                            | -                                      |
| scope         | String | `openid` 고정                                                          | -                                      |
| state         | String | 임의 문자열 (정해진 형식 없음)                                                   | -                                      |

#### 응답

쿼리 파라미터:

| 항목    | 타입     | 설명                      | 비고(예시) |
|-------|--------|-------------------------|--------|
| code  | String | 토큰 요청 시 필요한 인가 코드       | -      |
| state | String | 요청 시 전달한 state 값과 동일한 값 | -      |

#### 호출 예시

요청:

```http
GET https://www.saas.go.kr/epki/oauth2/authorize? \
client_id=${연계서비스ID} \
&redirect_uri=${REDIRECT_URI} \
&response_type=code \
&scope=openid \
&state=${STATE}
```

응답:

- 성공

```text
HTTP/1.1 302
Location: ${REDIRECT_URI}?code=${AUTHORIZE_CODE}&state=${STATE}
```

- 실패

```json
(1) 등록된 클라이언트 ID가 없을 때
{
  "traceId": "9b7396b60cf7894a",
  "code": "EA10004",
  "message": "등록된 Client ID가 없습니다",
  "status": "404 NOT_FOUND"
}

(2) 요청 파라미터 값이 부정확할 때
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청의 필수 항목 또는 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

#### 나. Token 요청 (인가 코드 교환)

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

#### 요청

헤더:

| 항목           | 설명                                | 비고(예시) |
|--------------|-----------------------------------|--------|
| Content-Type | application/x-www-form-urlencoded | -      |

본문:

| 항목            | 타입     | 설명                                                                   | 비고(예시)                                 |
|---------------|--------|----------------------------------------------------------------------|----------------------------------------|
| grant_type    | String | `authorization_code` 고정                                              | -                                      |
| client_id     | String | 연계 서비스 ID<br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196               |
| client_secret | String | API Key  <br/>- [운영관리] > [연계서비스] 에서 확인 가능                            | -                                      |
| redirect_uri  | String | 인가 코드 요청 시 사용한 URI                                                   | ex) http://testsaas.com/oauth/callback |
| code          | String | `/oauth2/authorize` 에서 발급받은 인가 코드                                    | -                                      |

#### 응답

본문:

| 항목            | 타입      | 설명                          | 비고(예시) |
|---------------|---------|-----------------------------|--------|
| access_token  | String  | 사용자 정보 조회, API 요청 등 인증에 사용  | -      |
| refresh_token | String  | Access Token 갱신에 사용         | -      |
| id_token      | String  | 로그아웃 시 사용                   | -      |
| scope         | String  | openid으로 고정                 | 고정값    |
| token_type    | String  | Bearer으로 고정                 | 고정값    |
| expires_in    | Integer | 3600 (Access Token 유효기간(s)) | -      |

#### 호출 예시

요청:

```bash
curl -X POST "https://saas.go.kr/epki/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=${CLIENT_ID}" \
  -d "client_secret=${CLIENT_SECRET}" \
  -d "redirect_uri=${REDIRECT_URI}" \
  -d "code=${AUTHORIZE_CODE}"
```

응답:

- 성공

```json
HTTP/1.1 200
Content-Type: application/json
{
"access_token": "${ACCESS_TOKEN}",
"refresh_token": "${REFRESH_TOKEN}",
"scope": "openid",
"id_token": "${ID_TOKEN}",
"token_type": "Bearer",
"expires_in": 3599
}
```

- 실패 (요청 파라미터 값이 부정확할 때):

```json
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청의 필수 항목 또는 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

**토큰 발급시 사용한 code로 토큰 발급 시 더 이상 사용할 수 없습니다. 잘못된 파라미터를 입력하여 에러가 나올 경우 code를 다시 발급받아야 합니다.**

---

### (2) 사용자 정보 조회

- 요청 방식: `GET`
- 요청 URI: `/userinfo`

#### 요청

헤더:

| 항목            | 설명                     | 비고(예시) |
|---------------|------------------------|--------|
| Authorization | Bearer ${ACCESS_TOKEN} | -      |

#### 응답

| 항목       | 타입     | 설명               | 비고(예시) |
|----------|--------|------------------|--------|
| cn       | String | 사용자 식별값          | -      |
| name     | String | 사용자 이름           | -      |
| instCode | String | 사용자가 속한 조직의 기관코드 | -      |

***응답 파라미터가 4월 이후 추가될 수 있습니다.**  
***응답 파라미터 변경 시 공지를 통해 추가 안내를 진행할 예정입니다.**

#### 호출 예시

요청:

```bash
curl -X GET "https://saas.go.kr/epki/userinfo" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

응답:

```json
HTTP/1.1 200
Content-Type: application/json
{
"name": "테스트 이름",
"cn": "테스트 CN",
"instCode": "테스트 기관코드"
}
```

실패 (유효하지 않은 토큰):

```json
{
  "traceId": "adf09ad4684fb220",
  "code": "EA30002",
  "message": "유효하지 않은 토큰입니다",
  "status": "401 UNAUTHORIZED"
}
```

---

### (3) Token 갱신

인증을 위한 Access Token의 유효 기간은 1시간입니다. <br/>Access Token이 만료되면 발급받은 리프래쉬 토큰으로 갱신 요청을 하여 엑세스 토큰을 재발급 받습니다.

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

#### 요청

헤더:

| 항목           | 설명                                | 비고(예시) |
|--------------|-----------------------------------|--------|
| Content-Type | application/x-www-form-urlencoded | -      |

본문:

| 항목            | 타입     | 설명                                                                  | 비고(예시)                   |
|---------------|--------|---------------------------------------------------------------------|--------------------------|
| grant_type    | String | refresh_token으로 고정                                                  | 고정값                      |
| client_id     | String | 연계서비스ID <br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196 |
| client_secret | String | API 키 <br/>- [운영관리] > [연계서비스] 에서 확인 가능                              | -                        |
| refresh_token | String | 최초 토큰 발급 시 응답받은 리프래쉬 토큰                                             | -                        |

#### 응답

본문:

| 항목            | 타입      | 설명                          | 비고(예시) |
|---------------|---------|-----------------------------|--------|
| access_token  | String  | 사용자 정보 조회, API 요청 등 인증에 사용  | -      |
| refresh_token | String  | Access Token 갱신에 사용         | -      |
| id_token      | String  | 로그아웃 시 사용                   | -      |
| scope         | String  | openid으로 고정                 | 고정값    |
| token_type    | String  | Bearer으로 고정                 | 고정값    |
| expires_in    | Integer | 3600 (Access Token 유효기간(s)) | -      |

#### 호출 예시

요청:

```bash
curl -X POST "https://saas.go.kr/epki/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=${CLIENT_ID}" \
  -d "client_secret=${CLIENT_SECRET}" \
  -d "refresh_token=${REFRESH_TOKEN}"
```

응답:

- 성공

```json
HTTP/1.1 200
Content-Type: application/json
{
"access_token": "${ACCESS_TOKEN}",
"refresh_token": "${NEW_REFRESH_TOKEN}",
"scope": "openid",
"id_token": "${ID_TOKEN}",
"token_type": "Bearer",
"expires_in": 3599
}
```

- 실패 (요청 파라미터 값이 부정확):

```json
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청의 필수 항목 또는 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

---

### (4) 로그아웃

- 요청 방식: `GET`
- 요청 URI: `/connect/logout`

#### 요청

쿼리 파라미터:

| 항목                       | 설명                                               | 비고(예시)                  |
|--------------------------|--------------------------------------------------|-------------------------|
| id_token_hint            | 토큰 발급 시 함께 전달 받은 ID_TOKEN 값                      | -                       |
| post_logout_redirect_uri | 이용 신청 때 민간 SaaS에서 지정한 post_logout_redirect_uri 값 | ex) http://testsaas.com |

#### 응답

민간 SaaS에서 지정한 ${POST_LOGOUT_REDIRECT_URI} 로 리다이렉트

#### 호출 예시

요청:

```http
https://saas.go.kr/epki/connect/logout?
id_token_hint=${ID_TOKEN}&
post_logout_redirect_uri=${POST_LOGOUT_REDIRECT_URI}
```

응답:

```json
HTTP/1.1 302
Location: ${POST_LOGOUT_REDIRECT_URI}
```

## 에러 코드

| 코드      | 에러 메시지                    | 상태코드 |
|---------|---------------------------|------|
| EA10001 | 시스템 할당로직 통신이 원활하지 않습니다    | 500  |
| EA10002 | 잘못된 요청입니다                 | 400  |
| EA10003 | 요청하신 데이터가 없습니다            | 404  |
| EA10004 | 등록된 Client ID가 없습니다       | 404  |
| EA10005 | 지원하지 않는 방식입니다             | 405  |
| EA20001 | 인증되지 않았습니다                | 401  |
| EA20002 | 접근 권한이 없습니다               | 403  |
| EA20003 | 요청의 필수 항목 또는 값이 잘못되었습니다   | 400  |
| EA20004 | OAuth2 인증 흐름을 통한 접근이 아닙니다 | 401  |
| EA30001 | 잘못된 토큰 형식입니다              | 400  |
| EA30002 | 유효하지 않은 토큰입니다             | 401  |
| EA30003 | 만료된 API키입니다               | 401  |
| EA30004 | 이용 상태를 확인하세요              | 401  |
| EA30005 | 테스트 키로 운영환경 이용은 불가합니다     | 401  |
