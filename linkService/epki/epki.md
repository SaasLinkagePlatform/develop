## EPKI 연계 SaaS

### 개요

EPKI 연계 SaaS는 교육부 산하 기관, 전국 시도 교육청, 학교 등 교육·연구기관 종사자들이 민간 SaaS에 로그인 시, <br/>
EPKI 인증서를 통해 로그인을 할 수 있도록 인증서 검증을 연계하는 서비스입니다. <br/>

EPKI 연계 로그인은 OAuth 2.0 프로토콜을 통해 서비스되고 있습니다.

- 개발 서버: `https://www.saas.go.kr/epki-stg`
- 운영 서버: `https://www.saas.go.kr/epki`


- 테스트 인증서가 따로 존재하지 않기 때문에, 개발 서버에서는 아래 두 가지 방식의 로그인을 제공합니다.
  - 인증서 없는 로그인 : 인증서 검증을 건너뛰고 임의의 사용자 정보로 로그인 수행
  - 실인증서 로그인 : 실제 인증서를 첨부하고, 해당 인증서의 사용자 정보로 로그인 수행

### API 목록

| 요청 URL | 메서드 | 응답 형식 | 설명 |
| --- | --- | --- | --- |
| `/oauth2/authorize` | GET | Redirect URI | 연계 API에 권한 인증을 요청하는 API |
| `/oauth2/token` | POST | JSON | 연계 API에 토큰을 요청(갱신)하는 API |
| `/userinfo` | GET | JSON | 로그인 한 사용자의 정보를 요청하는 API |
| `/connect/logout` | GET | Redirect URI | 로그인 한 사용자의 세션을 만료시키는 API |

### API 명세

#### 주요 파라미터 설명

```text
- `client_id`: 연계서비스ID
- `client_secret`: API Key
- `redirect_uri`: EPKI 연계 API에 인가 코드를 요청하고 리다이렉트되는 민간 SaaS의 서비스 URI
- `post_logout_redirect_uri`: 로그아웃 후 리다이렉트되는 민간 SaaS의 서비스 URI

- `client_id`, `client_secret`은 이용신청 승인 이후 통합관리포털에서 발급되며, `[운영관리] > [연계서비스]` 에서 확인 가능합니다.
- `redirect_uri`, `post_logout_redirect_uri`는 통합관리포털에서 이용 신청 시 입력합니다.
```

***[에러코드](#에러-코드)는 문서 최하단에 있습니다.**

---

### (1) 로그인

#### 가. 인가 코드 요청

- 요청 방식: `GET`
- 요청 URI: `/oauth2/authorize`

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| response_type | string | `code`로 고정 | 고정값 |
| scope | string | `openid`로 고정 | 고정값 |
| client_id | string | 연계서비스ID | ex) `LKSV2099010119000196` |
| redirect_uri | string | 서비스 신청 시 입력한 redirect_uri | ex) `http://testsaas.com/oauth/callback` |
| state | string | 랜덤 문자열 | 정해진 형식 없음 |

** redirect_uri는 통합관리포털에서 이용신청 시 입력하며, 이후 운영 메뉴([운영관리 > 연계서비스])에서 수정 및 추가 가능합니다.

##### 요청 예시

```text
GET https://www.saas.go.kr/epki/oauth2/authorize?
    client_id=${연계서비스ID}&
    redirect_uri=${REDIRECT_URI}&
    response_type=code&
    scope=openid&
    state=${STATE}
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| code | String | 토큰 요청 시 필요한 인가 코드 | - |
| state | String | 요청 시 전달한 state 값과 동일한 값 | - |

##### 응답 예시

- 성공

```text
HTTP/1.1 302
Content-Length: 0
Location: ${REDIRECT_URI}?code=${AUTHORIZE_CODE}&state=${STATE}
```

- 실패

```json
// 등록된 클랑언트 ID가 없을 때
{
  "traceId": "9b7396b60cf7894a",
  "code": "EA10004",
  "message": "등록된 Client ID가 없습니다",
  "status": "404 NOT_FOUND"
}
```

```json
// 요청 파라미터 값이 부정확할 때
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청에 필요한 항목이나 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

#### 나. Token 요청

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

##### 요청 헤더

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Content-Type | `application/x-www-form-urlencoded` | 고정값 |

##### 요청 본문

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| grant_type | string | `authorization_code`으로 고정 | 고정값 |
| client_id | string | 연계서비스ID | ex) `LKSV2099010119000196` |
| client_secret | string | API 키 | - |
| redirect_uri | string | 서비스 신청 시 입력한 redirect_uri | ex) `http://testsaas.com/oauth/callback` |
| code | string | 발급받은 인가코드 | - |

##### 요청 예시

```text
curl -X POST "https://saas.go.kr/epki/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=${연계서비스ID}" \
  -d "client_secret=${API_KEY}" \
  -d "redirect_uri=${REDIRECT_URI}" \
  -d "code=${AUTHORIZE_CODE}"
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| access_token | string | 사용자 정보 조회, API 요청 등 인증에 사용 | - |
| refresh_token | string | Access Token 갱신에 사용 | - |
| id_token | string | 로그아웃 시 사용 | - |
| scope | string | `openid`로 고정 | 고정값 |
| token_type | string | `Bearer`로 고정 | 고정값 |
| expires_in | string | 3600 (Access Token 유효기간(s)) | - |

##### 응답 예시

- 성공

```text
HTTP/1.1 200
Content-Type: application/json
```

```json
{
  "access_token": ${ACCESS_TOKEN},
  "refresh_token": ${REFRESH_TOKEN},
  "scope": "openid",
  "id_token": ${ID_TOKEN},
  "token_type": "Bearer",
  "expires_in": 3599
}
```

- 실패

```json
// 요청 파라미터 값이 부정확할 때
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청에 필요한 항목이나 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

**토큰 발급시 사용한 code로 토큰 발급 시 더 이상 사용할 수 없습니다. 잘못된 파라미터를 입력하여 에러가 나올 경우 code를 다시 발급받아야 합니다.**

---

### (2) 사용자 정보 조회

로그인한 사용자의 정보를 Access Token으로 조회할 수 있습니다.

- 요청 방식: `GET`
- 요청 URI: `/userinfo`

##### 요청 헤더

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Authorization | `Bearer ${Access Token}` | - |

##### 요청 예시

```text
curl -X GET "https://saas.go.kr/epki/userinfo" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| cn | string | 사용자 식별값 | - |
| name | string | 사용자 이름 | - |
| instCode | string | 사용자가 속한 조직의 기관코드 | 인증서 발급 당시의 조직 기관 코드 |

```text
* 현재는 인증서 사용자의 인증서 발급 당시 기관코드를 전달해주고 있으나,
  EPKI 인증서를 주관하는 교육부 행정전자서명인증센터에서 API를 고도화하게 되면 현행화된 기관코드를 제공할 수 있습니다.
* 고도화는 4월에 진행될 예정이며, 이에 따라 응답 파라미터가 추가될 수 있습니다.
* 추가될 응답 파라미터는 (현행화된) 기관코드, 기관명, 상위기관코드, 상위기관명입니다.
* 정확한 일정과 파라미터 등이 정해지면 다시 한번 안내해드리겠습니다.
```

##### 응답 예시

- 성공

```text
HTTP/1.1 200
Content-Type: application/json
```

```json
{
  "name": "테스트 이름",
  "cn": "테스트 CN",
  "instCode": "테스트 기관코드"
}
```

- 실패

```json
// 유효하지 않은 토큰일 때
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

##### 요청 헤더

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Content-Type | `application/x-www-form-urlencoded` | 고정값 |

##### 요청 본문

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| grant_type | string | `refresh_token`으로 고정 | 고정값 |
| client_id | string | 연계서비스ID | ex) `LKSV2099010119000196` |
| client_secret | string | API 키 | - |
| refresh_token | string | 토큰 발급 시 응답받은 리프래쉬 토큰 | - |

##### 요청 예시

```text
curl -X POST "https://saas.go.kr/epki/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=${연계서비스ID}" \
  -d "client_secret=${API_KEY}" \
  -d "refresh_token=${REFRESH_TOKEN}"
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| access_token | string | 사용자 정보 조회, API 요청 등 인증에 사용 | - |
| refresh_token | string | Access Token 갱신에 사용 | - |
| id_token | string | 로그아웃 시 사용 | - |
| scope | string | `openid`로 고정 | 고정값 |
| token_type | string | `Bearer`로 고정 | 고정값 |
| expires_in | string | 3600 (Access Token 유효기간(s)) | - |

##### 응답 예시

- 성공

```text
HTTP/1.1 200
Content-Type: application/json
```

```json
{
  "access_token": ${ACCESS_TOKEN},
  "refresh_token": ${REFRESH_TOKEN},
  "scope": "openid",
  "id_token": ${ID_TOKEN},
  "token_type": "Bearer",
  "expires_in": 3599
}
```

- 실패

```json
// 요청 파라미터 값이 부정확할 때
{
  "traceId": "57535d1f3a5089c4",
  "code": "EA20003",
  "message": "요청에 필요한 항목이나 값이 잘못되었습니다",
  "status": "400 BAD_REQUEST"
}
```

---

### (4) 로그아웃

- 요청 방식: `GET`
- 요청 URI: `/connect/logout`

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| id_token_hint | string | 토큰 발급 시 함께 전달 받은 ID_TOKEN 값 | - |
| post_logout_redirect_uri | string | 이용 신청 때 지정한 `post_logout_redirect_uri` 값 | ex) `http://testsaas.com` |

##### 요청 예시

```text
GET https://www.saas.go.kr/epki/connect/logout?
    id_token_hint=${ID_TOKEN}&
    post_logout_redirect_uri=${POST_LOGOUT_REDIRECT_URI}
```

##### 응답

- 민간 SaaS에서 지정한 `${POST_LOGOUT_REDIRECT_URI}` 로 리다이렉트

---
## 에러 코드

| 코드 | 에러메시지 | 상태코드 |
| --- | --- | --- |
| EA10001 | 시스템 장애로 통신이 원활하지 않습니다 | 500 |
| EA10002 | 잘못된 요청입니다 | 400 |
| EA10003 | 요청하신 데이터가 없습니다 | 404 |
| EA10004 | 등록된 Client ID가 없습니다 | 404 |
| EA10005 | 지원하지 않는 형식입니다 | 405 |
| EA20001 | 인증되지 않았습니다 | 401 |
| EA20002 | 접근이 권한이 없습니다 | 403 |
| EA20003 | 요청의 필수 항목 또는 값이 잘못되었습니다 | 400 |
| EA20004 | OAuth2 인증 흐름을 통한 접근이 아닙니다 | 401 |
| EA30001 | 잘못된 토큰 형식입니다 | 400 |
| EA30002 | 유효하지 않은 토큰입니다 | 401 |
| EA30003 | 유효하지 않은 API키입니다 | 401 |
| EA30004 | 이용 상태를 확인하세요 | 401 |
| EA30005 | 테스트 키로 운영 환경 이용은 불가합니다 | 401 |
