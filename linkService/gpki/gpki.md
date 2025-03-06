## GPKI 연계 SaaS 

### 개요

GPKI 연계 SaaS는 중앙 및 지방 행정 기관에 종사하는 공직자들이 민간 SaaS에 로그인 시, <br/>
GPKI 인증서를 통해 로그인을 할 수 있도록 인증서 검증을 연계하는 서비스입니다.

GPKI 연계 로그인은 OAuth 2.0 프로토콜을 통해 서비스되고 있습니다.

- 개발 서버 : https://www.saas.go.kr/gpki-stg 
- 운영 서버 : https://www.saas.go.kr/gpki


- 테스트 인증서가 따로 존재하지 않기 때문에, 개발 서버에서는 아래 두 가지 방식의 로그인 제공합니다.
  - 인증서 없는 로그인 : 인증서 검증을 건너뛰고 임의의 사용자 정보로 로그인 수행
  - 실인증서 로그인 : 실제 인증서를 첨부하고, 해당 인증서의 사용자 정보로 로그인 수행

### API 목록

| 요청 URL            | 메서드  | 응답 형식        | 설명                                            |
|-------------------|------|--------------|-----------------------------------------------|
| /oauth2/authorize | GET  | Redirect URI | 연계 SaaS에 인가 코드를 요청하는 API                      |
| /oauth2/token     | POST | JSON         | 연계 SaaS에 Access Token/Refresh Token을 요청하는 API |
| /userinfo         | GET  | JSON         | 로그인 한 사용자의 정보를 요청하는 API                       |
| /connect/logout   | GET  | Redirect URI | 로그인 한 사용자의 토큰을 만료시키고 세션 정보를 삭제하는 API          |

### 주요 파라미터 설명
```
- client_id, client_secret은 이용신청 승인 이후 통합관리포털에서 발급되며, [운영관리] > [연계서비스] 에서 확인 가능합니다.

- client_id: 연계서비스ID

- client_secret: API Key

- redirect_uri, post_logout_redirect_uri은 통합관리포털에서 이용 신청 시 입력합니다.

- redirect_uri : GPKI 연계 SaaS에게 인가 코드를 요청하고 리다이렉트되는 민간 SaaS의 서비스 URI

- post_logout_redirect_uri : 로그아웃 후 리다이렉트되는 민간 SaaS의 서비스 URI
```

***[에러코드](#에러-코드)는 문서 최하단에 있습니다**


### (1) 로그인
#### 가. 인가 코드 요청
- 요청 방식 : GET
- 요청 URI : /oauth2/authorize

#### 요청
&nbsp;&nbsp; 쿼리 파라미터

| 항목            | 타입     | 설명                                                                  | 비고(예시)                                 |
|---------------|--------|---------------------------------------------------------------------|----------------------------------------|
| client_id     | String | 연계서비스ID <br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196               |
| redirect_uri  | String | 인가 코드를 전달받을 서비스 서버의 URI<br/> - 서비스 신청 시 입력한 redirect_uri<br/>       | ex) http://testsaas.com/oauth/callback |
| response_type | String | code로 고정                                                            | 고정값                                    |
| scope         | String | openid로 고정                                                          | 고정값                                    |
| state         | String | 임의의 문자열 (정해진 형식 없음)                                                 |                                        |

#### 응답
&nbsp;&nbsp; 쿼리 파라미터

| 항목    | 타입     | 설명                      | 비고(예시) |
|-------|--------|-------------------------|--------|
| code  | String | 토큰 요청 시 필요한 인가 코드       |        
| state | String | 요청 시 전달한 state 값과 동일한 값 |        |

#### 호출 예시
###### 요청
```sybase
https://www.saas.go.kr/gpki/oauth2/authorize?client_id=${연계서비스ID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=openid&state=${STATE}
```  
###### 응답
&nbsp;&nbsp; 성공
```sybase
HTTP/1.1 302
Content-Length: 0
Location: ${REDIRECT_URI}?code=${AUTHORIZE_CODE}&state=${STATE}
```

&nbsp;&nbsp; 실패
```
(1) 등록된 클라이언트 ID가 없을 때

{
    "traceId": "9b7396b60cf7894a",
    "code": "GA10004",
    "message": "등록된 Client ID가 없습니다",
    "status": "404 NOT_FOUND"
}


(2) 요청 파라미터 값이 부정확할 때

{
    "traceId":"57535d1f3a5089c4",
    "code":"GA20003",
    "message":"요청에 필요한 항목이나 값이 잘못되었습니다",
    "status":"400 BAD_REQUEST"
}
```

---
#### 나. Token 요청
- 요청 방식 : POST
- 요청 URI : /oauth2/token

#### 요청
&nbsp;&nbsp; 헤더

| 항목           | 설명                                | 비고(예시) |
|--------------|-----------------------------------|--------|
| Content-Type | application/x-www-form-urlencoded | -      |


&nbsp;&nbsp; 본문

| 항목            | 타입     | 설명                                                                  | 비고(예시)                                 |
|---------------|--------|---------------------------------------------------------------------|----------------------------------------|
| grant_type    | String | authorization_code으로 고정                                             | 고정값                                    |
| client_id     | String | 연계서비스ID <br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196               |
| client_secret | String | API 키 <br/>- [운영관리] > [연계서비스] 에서 확인 가능                              | -                                      |
| redirect_uri  | String | 서비스 신청 시 입력한 redirect_uri                                           | ex) http://testsaas.com/oauth/callback |
| code          | String | 발급 받은 인가 코드                                                         |                                        |

#### 응답
&nbsp;&nbsp; 본문

| 항목            | 타입      | 설명                          | 비고(예시) |
|---------------|---------|-----------------------------|--------|
| access_token  | String  | 사용자 정보 조회, API 요청 등 인증에 사용  | -      |
| refresh_token | String  | Access Token 갱신에 사용         | -      |
| id_token      | String  | 로그아웃 시 사용                   | -      |
| scope         | String  | openid으로 고정                 | 고정값    |
| token_type    | String  | Bearer으로 고정                 | 고정값    |
| expires_in    | Integer | 3600 (Access Token 유효기간(s)) | -      |

#### 호출 예시
###### 요청
```bash
curl -X POST "https://saas.go.kr/gpki/oauth2/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=authorization_code" \
    -d "client_id=${연계서비스ID}" \
    -d "client_secret=${API_KEY}"
    -d "redirect_uri=${REDIRECT_URI}" \
    -d "code=${AUTHORIZE_CODE}"
```
###### 응답
&nbsp;&nbsp; 성공
```sybase
HTTP/1.1 200
Content-Type: application/json
{
    "access_token": ${ACCESS_TOKEN},
    "refresh_token": ${REFRESH_TOKEN},
    "scope": "openid",
    "id_token": ${ID_TOKEN},
    "token_type": "Bearer",
    "expires_in": 3599
}
```

&nbsp;&nbsp; 실패 - 요청 파라미터 값이 부정확할 때
```json

{
    "traceId":"57535d1f3a5089c4",
    "code":"GA20003",
    "message":"요청에 필요한 항목이나 값이 잘못되었습니다",
    "status":"400 BAD_REQUEST"
}
```
---
### (2) 사용자 정보 조회
로그인한 사용자의 정보를 Access Token으로 조회할 수 있습니다.

- 요청 방식 : GET
- 요청 URI : /userinfo

#### 요청
&nbsp;&nbsp; 헤더

| 항목            | 설명                     | 비고(예시) |
|---------------|------------------------|--------|
| Authorization | Bearer ${ACCESS_TOKEN} | -      |

#### 응답
&nbsp;&nbsp; 본문

| 항목       | 타입     | 설명               | 비고(예시) |
|----------|--------|------------------|--------|
| cn       | String | 사용자 식별값          | -      |
| name     | String | 사용자 이름           | -      |
| instCode | String | 사용자가 속한 조직의 기관코드 | -      |

#### 호출 예시
###### 요청
```bash
curl -X GET "https://saas.go.kr/gpki/userinfo" \
    -H "Authorization: Bearer ${ACCESS_TOKEN}" 
```
###### 응답
&nbsp;&nbsp; 성공
```sybase
HTTP/1.1 200
Content-Type: application/json
{
    "name": "홍길동",
    "cn": "100홍길동100",
    "instCode": "1000000"
}
```
&nbsp;&nbsp; 실패 - 유효하지 않은 토큰일 때
```json
{
    "traceId": "adf09ad4684fb220",
    "code": "GA30002",
    "message": "유효하지 않은 토큰입니다",
    "status": "401 UNAUTHORIZED"
}
```

---
### (3) Token 갱신
인증을 위한 Access Token의 유효 기간은 1시간입니다. <br/>Access Token이 만료되면 발급받은 리프래쉬 토큰으로 갱신 요청을 하여 엑세스 토큰을 재발급 받습니다.
- 요청 방식 : POST
- 요청 URI : /oauth2/token

#### 요청
&nbsp;&nbsp; 헤더

| 항목           | 설명                                | 비고(예시) |
|--------------|-----------------------------------|--------|
| Content-Type | application/x-www-form-urlencoded | -      |


&nbsp;&nbsp; 본문

| 항목            | 타입     | 설명                                                                  | 비고(예시)                   |
|---------------|--------|---------------------------------------------------------------------|--------------------------|
| grant_type    | String | refresh_token으로 고정                                                  | 고정값                      |
| client_id     | String | 연계서비스ID <br/> - 서비스 신청 후 발급받는 서비스ID<br/>- [운영관리] > [연계서비스] 에서 확인 가능 | ex) LKSV2099010119000196 |
| client_secret | String | API 키 <br/>- [운영관리] > [연계서비스] 에서 확인 가능                              | -                        |
| refresh_token | String | 최초 토큰 발급 시 응답받은 리프래쉬 토큰                                             | -                        |

#### 응답
&nbsp;&nbsp; 본문

| 항목            | 타입      | 설명                          | 비고(예시) |
|---------------|---------|-----------------------------|--------|
| access_token  | String  | 사용자 정보 조회, API 요청 등 인증에 사용  | -      |
| refresh_token | String  | Access Token 갱신에 사용         | -      |
| id_token      | String  | 로그아웃 시 사용                   | -      |
| scope         | String  | openid으로 고정                 | 고정값    |
| token_type    | String  | Bearer으로 고정                 | 고정값    |
| expires_in    | Integer | 3600 (Access Token 유효기간(s)) | -      |

#### 호출 예시
###### 요청
```bash
curl -X POST "https://saas.go.kr/gpki/oauth2/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=refresh_token" \
    -d "client_id=${연계서비스ID}" \
    -d "client_secret=${API_KEY}"
    -d "refresh_token=${REFRESH_TOKEN}" 
```
###### 응답
&nbsp;&nbsp; 성공
```sybase
HTTP/1.1 200
Content-Type: application/json
{
    "access_token": ${ACCESS_TOKEN},
    "refresh_token": ${REFRESH_TOKEN},
    "scope": "openid",
    "id_token": ${ID_TOKEN},
    "token_type": "Bearer",
    "expires_in": 3599
}
```
---
### (3) 로그아웃
- 요청 방식 : GET
- 요청 URI : /connect/logout

#### 요청
&nbsp;&nbsp; 쿼리 파라미터

| 항목                       | 설명                                               | 비고(예시)                  |
|--------------------------|--------------------------------------------------|-------------------------|
| id_token_hint            | 토큰 발급 시 함께 전달 받은 ID_TOKEN 값                      | -                       |
| post_logout_redirect_uri | 이용 신청 때 민간 SaaS에서 지정한 post_logout_redirect_uri 값 | ex) http://testsaas.com |

#### 응답
- 민간 SaaS에서 지정한 ${POST_LOGOUT_REDIRECT_URI} 로 리다이렉트

#### 호출 예시
###### 요청
```sybase
https://www.saas.go.kr/gpki/connect/logout?id_token_hint=${ID_TOKEN}&post_logout_redirect_uri=${POST_LOGOUT_REDIRECT_URI}
```  
###### 응답
```sybase
HTTP/1.1 302
Content-Length: 0
Location: ${POST_LOGOUT_REDIRECT_URI}
```
---

## 에러 코드

| 코드      | 에러 메시지                    | 상태코드 |
|---------|---------------------------|------|
| GA10001 | 시스템 장애로 통신이 원활하지 않습니다     | 500  |
| GA10002 | 잘못된 요청입니다                 | 400  |
| GA10003 | 요청하신 데이터가 없습니다            | 404  |
| GA10004 | 등록된 Client ID가 없습니다       | 404  |
| GA10005 | 지원하지 않는 형식입니다             | 405  |
| GA20001 | 인증되지 않았습니다                | 401  |
| GA20002 | 접근이 권한이 없습니다              | 403  |
| GA20003 | 요청에 필요한 항목이나 값이 잘못되었습니다   | 400  |
| GA20004 | OAuth2 인증 흐름을 통한 접근이 아닙니다 | 401  |
| GA30001 | 잘못된 토큰 형식입니다              | 400  |
| GA30002 | 유효하지 않은 토큰입니다             | 401  |
| GA30003 | 유효하지 않은 API키입니다           | 401  |

