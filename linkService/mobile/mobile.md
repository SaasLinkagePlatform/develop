## 모바일공무원인증
- 브라우저에 민간SaaS 로그인이 되어 있으면, 다른 민간SaaS에서는 로그인 불필요. (단, 동일 브라우저 내 탭에서만 가능)
- 개발용 서버 : https://www.saas.go.kr/auth-stg (테스트 공무원증이 있거나, 개발할 때 사용)
- 운영용 서버 : https://www.saas.go.kr/auth (테스트 공무원증 사용 불가)


## 모바일공무원인증 API 목록

| 요청 URL               | 메서드 | 응답 형식 | 설명                                                        |
|------------------------|--------|-----------|-------------------------------------------------------------|
| /oauth2/authorize       | GET    | JSON      | 연계 SaaS에게 권한 인증을 요청하는 API                      |
| /oauth2/token           | POST   | JSON      | 연계 SaaS에 Access Token/Refresh Token을 요청하는 API       |
| /oauth2/userinfo        | GET    | JSON      | 로그인 한 사용자의 정보를 요청하는 API                     |
| /oauth2/connect/logout  | GET    | JSON      | 로그인 한 사용자의 토큰을 만료시키는 API                    |  



---
### (1) 로그인
#### 가. Authorization Code 요청
- 요청 방식 : Redirection URI
- 요청 URI : /oauth2/authorize
- 요청 파라미터

| 파라미터명     | 입력값             | 비고(예시)                              |
|----------------|--------------------|-----------------------------------------|
| response_type  | code               | 고정값                                   |
| scope          | openid             | 고정값                                   |
| client_id      | 지정한 client_id   | 연계서비스ID (ex: LKSV2099010119000196)  |
| redirect_uri   | 지정한 redirect_uri| 민간SaaS.com/oauth/callback             |
| state          | 랜덤 문자열        |                                         |
| nonce          | 랜덤 문자열        |                                         |  
** 고정값: 문자 그대로 입력(ex: code, openid)

```
  <요청 URI 예시>
  https://www.saas.go.kr/auth/oauth2/authorize? \\
  response_type=code& \\
  scope=openid& \\
  client_id=연계서비스ID& \\
  redirect_uri=민간SaaS.com/oauth/callback& \\
  state=gdyV_sdDS6VAFObL8WRBl& \\
  nonce=BVUPBg5OmnGsAUceNsDUh \\
  ```  

- Redirection URI
  [지정한 redirect_uri]?code=[Authorization Code]&state=[입력한 state]
```
  예시)
  https://민간SaaS.com(지정한 redirect_url) /oauth/callback? \\
  code=sMTesyQ8…kGxT-65FNcQnqji28qG4n& \\
  state=gdyV_sdDS6VAFObL8WRBl \\
  ```

- 프로세스
    - 로그인이 되어 있지 않았으면 로그인 페이지로 이동, 로그인 페이지에서 QR 인증을 하여 로그인이 성공하면
      redirect_uri 페이지로 이동한다.
    - 로그인이 되어 있는 경우 즉시 redirect_uri 페이지로 이동한다.


- 사후 처리
    - Redirection URI에서 취득한 Authorization Code를 사용하여 Token을 발급받는다.


#### 나. Token 요청
- 요청 방식 : POST
- 요청 URI : /oauth2/token
- 요청 파라미터

| 파라미터명       | 입력값                          | 비고(예시)                                         |
|------------------|---------------------------------|----------------------------------------------------|
| grant_type       | authorization_code              | 고정값                                              |
| client_id        | 지정한 client_id                | 연계서비스ID (ex: LKSV2099010119000196)             |
| client_secret    | 지정한 client_secret            | API키 (ex: 2098801797380751aA6a7dUhHJw004sYsJhWA+0Ji0leU5nTq87Mx0PfsOiQDZezm3Qa+GVaanmhJQ82) |
| redirect_uri     | 지정한 redirect_uri             | 민간SaaS.com/oauth/callback                        |
| code             | 발급 받은 code                  | 토큰 연장 할 땐 사용 안 함 (ex: sMTesyQ8…FNcQnqji28qG4n) |
** 고정값: 문자 그대로 입력(ex: authorization_code)


- 응답 항목

| 응답 항목      | 비고                                          |
|----------------|-----------------------------------------------|
| access_token   | API 요청 등의 인증에 사용                    |
| refresh_token  | Access Token 갱신에 사용                      |
| id_token       | 로그아웃 시 사용                              |
| scope          | openid (고정값)                               |
| token_type     | Bearer (고정값)                               |
| expires_in     | 3000 (Access Token 유효기간(s))               |


- 사후 처리
    - 응답 받은 access_token, refresh_token, id_token 값을 Cookie, LocalStorage 등에 저장한다.
    - Access Token을 사용하여 로그인한 사용자 정보를 조회한다.


#### 다. 사용자 정보 조회
- 요청 방식 : GET
- 요청 URI : /oauth2/userinfo
- 요청 헤더

| 이름          | 입력값                    | 비고(예시)                 |
|---------------|---------------------------|---------------------------|
| Authorization | Bearer [Access Token]     |                           |


- 응답 항목

| 이름       | 비고                          |
|------------|-------------------------------|
| cn         | 사용자식별값                   |
| name       | 사용자 이름                    |
| inst_code  | 사용자가 속한 부서코드 (조직도 정보 획득에 사용 가능) |

- 사용자 정보 조회를 로그인 절차에 포함했는데, 로그인 프로세스 외에 사용자 정보 조회가 가능하다.

---
### (2) Token 갱신
인증을 위한 Access Token의 유효기간은 1시간이다. Access Token이 만료되면 갱신요청을 하여 Token을 갱신
해야 한다.
- 요청 방식 : POST
- 요청 URI : /oauth2/token
- 요청 파라미터

| 파라미터명       | 입력값                          | 비고(예시)                          |
|------------------|---------------------------------|-------------------------------------|
| grant_type       | authorization_code              | 고정값                               |
| client_id        | 지정한 client_id                | 연계서비스ID                         |
| client_secret    | 지정한 client_secret            | API키                               |
| refresh_token    | 저장한 refresh_token            |                                     |

- 응답 항목

| 응답 항목     | 비고                                         |
|---------------|----------------------------------------------|
| access_token  | API 요청 등의 인증에 사용                   |
| refresh_token | Access Token 갱신에 사용                     |
| id_token      | 로그아웃 시 사용                             |
| scope         | openid (고정값)                              |
| token_type    | Bearer (고정값)                              |
| expires_in    | 3000 (Access Token 유효기간(s))              |

- 사후 처리
    - 갱신받은 access_token, refresh_token, id_token 값을 Cookie, Local Storage 등에 저장한다.

---
### (3) 로그아웃
- 요청 방식 : Redirection URI
- 요청 URI : /oauth2/connect/logout
- 요청 파라미터

| 파라미터명               | 입력값                | 비고(예시)                        |
|--------------------------|-----------------------|-----------------------------------|
| id_token_hint            | 저장된 id_token       |                                   |
| post_logout_redirect_uri | 지정한 redirect_uri   |                                   |

- 사후 처리
    - 로그아웃에 성공하면 지정한 redirect_uri 페이지로 이동되며, 저장된 access_token, refresh_token, id_token
      을 삭제하도록 한다.

---
```
※ 사전 조사
- client_id, client_secret, redirect_uri, post_logout_redirect_uri 등이 연계SaaS DB에 저장된다.
- client_id, client_secret은 이용신청 승인 이후 통합관리포털에서 발급된다. (client_id: 연계서비스ID, client_
  secret: API Key)
- redirect_uri, post_logout_redirect_uri은 통합관리포털에서 이용신청 시 입력한다. (여러 개 입력 가능, 입력
  후 수정 불가)
- 잘못된 client_id, client_secret으로 요청하거나 등록되지 않은 redirect_uri를 입력하면 정상적으로 서비스가
  제공되지 않음에 유의해야 한다.
```


