## 모바일공무원인증
- 브라우저에 민간SaaS 로그인이 되어 있으면, 다른 민간SaaS에서는 로그인 불필요. (단, 동일 브라우저 내 탭에서만 가능)
- 개발용 서버 : https://www.saas.go.kr/auth-stg (테스트 공무원증이 있거나, 개발할 때 사용)
- 운영용 서버 : https://www.saas.go.kr/auth (테스트 공무원증 사용 불가)


## 모바일공무원인증 API 목록

| 요청 URL                 | 메서드  | 응답 형식 | 설명                                            |
|------------------------|------|-----------|-----------------------------------------------|
| /oauth2/authorize      | GET  | JSON      | 연계 SaaS에게 권한 인증을 요청하는 API                     |
| /oauth2/token          | POST | JSON      | 연계 SaaS에 Access Token/Refresh Token을 요청하는 API |
| /oauth2/userinfo       | GET  | JSON      | 로그인 한 사용자의 정보를 요청하는 API                       |
| /oauth2/connect/logout | GET  | JSON      | 로그인 한 사용자의 토큰을 만료시키는 API                      |
| /mobile/txCheck        | POST | JSON      | App to App 로그인 시 공무원증 검증 성공 여부를 결과를 요청하는 API  |
| /login                 | POST | JSON      | App to App 로그인 요청을 보내는 API                    |

---
### (1) 로그인
#### 가. Authorization Code 요청
- 요청 방식 : Redirection URI
- 요청 URI : /oauth2/authorize
- 요청 파라미터

| 파라미터명                 | 입력값              | 비고(예시)                              |
|-----------------------|------------------|-------------------------------------|
| response_type         | code             | 고정값                                 |
| scope                 | openid           | 고정값                                 |
| client_id             | 지정한 client_id    | 연계서비스ID (ex: LKSV2099010119000196)  |
| redirect_uri          | 지정한 redirect_uri | 민간SaaS.com/oauth/callback           |
| state                 | 랜덤 문자열           |                                     |
| nonce                 | 랜덤 문자열           |                                     |
| web_login             | true/false       | App to App 인증의 경우에만 false(기본값:true) |
| code_challenge        | 랜덤 문자열           | code_verifier의 해쉬 값                 |
| code_challenge_method | S256             | 고정값                                 |

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
  web_login=true \\ 생략가능
```
```
  <App to App 요청 URI 예시>
  https://www.saas.go.kr/auth/oauth2/authorize? \\
  response_type=code& \\
  scope=openid& \\
  client_id=연계서비스ID& \\
  redirect_uri=민간SaaS.com/oauth/callback& \\
  state=gdyV_sdDS6VAFObL8WRBl& \\
  nonce=BVUPBg5OmnGsAUceNsDUh \\
  web_login=false \\
  code_challenge=dCQFMa2pb0o-AbGUNFprKzbwZkE5w6icdtDlJsvUwKI \\
  code_challenge_mehtod=S256\\
```

- Redirection URI
  [지정한 redirect_uri]?code=[Authorization Code]&state=[입력한 state]
```
  예시)
  https://민간SaaS.com(지정한 redirect_url) /oauth/callback? \\
  code=sMTesyQ8…kGxT-65FNcQnqji28qG4n& \\
  state=gdyV_sdDS6VAFObL8WRBl \\
  ```

- 일반 로그인 프로세스
    - 로그인이 되어 있지 않았으면 로그인 페이지로 이동, 로그인 페이지에서 QR 인증을 하여 로그인이 성공하면
      redirect_uri 페이지로 이동한다.
    - 로그인이 되어 있는 경우 즉시 redirect_uri 페이지로 이동한다.

- App to App 로그인 프로세스
    - 로그인이 되어 있지 않았으면 App Login QR Data 제공, 제공받은 데이터로 App 인증을 요청한다.
    - 인증 요청 후 검증 완료 여부 확인을 위한 API를 호출한다.
    - 완료 시 반환되는 값을 통해 로그인 요청을 보내고, 로그인 성공 시 즉시 redirect_uri 페이지로 이동한다.

- 사후 처리
    - Redirection URI에서 취득한 Authorization Code를 사용하여 Token을 발급받는다.


#### 나. Token 요청
- 요청 방식 : POST
- 요청 URI : /oauth2/token
- 요청 파라미터

| 파라미터명         | 입력값                | 비고(예시)                                                                                      |
|---------------|--------------------|---------------------------------------------------------------------------------------------|
| grant_type    | authorization_code | 고정값                                                                                         |
| client_id     | 지정한 client_id      | 연계서비스ID (ex: LKSV2099010119000196)                                                          |
| client_secret | 지정한 client_secret  | API키 (ex: 2098801797380751aA6a7dUhHJw004sYsJhWA+0Ji0leU5nTq87Mx0PfsOiQDZezm3Qa+GVaanmhJQ82) |
| redirect_uri  | 지정한 redirect_uri   | 민간SaaS.com/oauth/callback                                                                   |
| code          | 발급 받은 code         | 토큰 연장 할 땐 사용 안 함 (ex: sMTesyQ8…FNcQnqji28qG4n)                                              |
| code_verifier | 랜덤 문자열             | App to App 요청 시 사용                                                                          |
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

### (4) App to App
- 모바일 공무원증 App to App 가이드는 Mobile Web Browser에서 모바일 공무원증 Application 호출하는 방법만 안내합니다.
- 이용기관에서 적용하는 정책(지원하는 디바이스, 지원하는 브라우저 등)에 따라서 모바일
  Web To App을 적용하여 주시기 바랍니다.
- CSS, javascript, Angular, React, Vue.js 등의 browser에서 발생하는 오류는 기술 지원해드릴 수 없음을 알려드립니다.

#### (1) Android
##### 1. Host App 호출 정보

| Client App 설정 정보               | 입력값                |
|--------------------------|-----------------------|
| Client App에서 사용할 Scheme 값            | Bmc       |
| Client App에서 사용할 Action 값 | kr.go.id.bmc.VERIFY_VP    |

##### 2. App to App 연동 Flow
#### 가.구현 가이드
- 로그인 요청 후 응답 받은 결과 값을 사전 정의된 Scheme를 통해 Host App으로 전달
- appName = barotalk 은 Client App에서 정의된 앱 이름을 의미함
    
```
    ① ActivityResultLauncher<Intent> resultLauncher;
    ② resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),activityResultCallback);
    ③ Uri uri = Uri.parse("bmc://verify_vp?appName=barotalk
                            &type=VERIFY(고정값)
                            &spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ
                            &serviceCode=spdriver(민간 SaaS 서비스 코드)
                            &callBackUrl=https://www.saas.go.kr/auth/spnoneprofile/verify(고정값으로 전달됨)
                            &nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460
                            &encryptType=2(고정값)”);
    ④ Intent intent = new Intent("kr.go.id.bmc.VERIFY_VP", uri);
    ⑤ resultLauncher.launch(intent);
    
    1. Activity 전역에 런처 클래스 선언
    2. onCreate() 실행 시 런처 등록
    3. Host App 에서 검증시 사용할 데이타
    4. Host App AndroidManifest.xml 에 정의된 Action 값. 
    5. Host App 호출
```
- Host App에서 검증 결과를 Client App으로 전달
```
    결과 예시) bmc://verify_vp?result=true&code=200&message=success&errorMsg=
    ActivityResultCallback<ActivityResult> activityResultCallback = new ActivityResultCallback<ActivityResult>() {
     @Override
     public void onActivityResult(ActivityResult result) {
         int resultCode = result.getResultCode();
         if (resultCode == RESULT_OK) {
             Intent data = result.getData();
             if (data != null) {
                 ①String strResult = data.getData().getQueryParameter("result");
                 ②if ("true".equals(strResult)) {
                     // Success 처리
                     Log.i("TAG", "success");
                   } else {
                     // Fail 처리
                     Log.i("TAG", "fail");
                   }
             }
          }
        }
     };
     1. Host App에서 결과 값으로 전달한 파라미터
     2. Client App에서 결과 처리
```
#### (2) iOS
1. Project info.plist에 URL types 정의
- 유니크한 Identifier값을 설정합니다.URL Schcmes에 불려질 앱의 scheme을 설정한다.
```
    <key>CFBundleURLTypes</key>
    <array>
    <dict>
       <key>CFBundleURLName</key>
       <string>유니크한 Identifier 값 설정</string>
       <key>CFBundleURLSchemes</key>
       <array>
          <string>유니크한 scheme 설정(불려질 앱의 이름)</string>
       </array>
    </dict>
    </array>
```
- canOpenURL 메소드를 사용하여 앱 호출가능 여부를 체크할 경우
- info.plist에 LSApplicationQueriesSchemes 배열에 해당 앱을 부를 앱의 scheme을 추가 canOpenURL을 호출하지 않고 바로 openURL을 사용하여도 무방합니다

2. 연동 Flow
- 로그인 요청 응답 받은 결과 값을 사전 정의된 Scheme를 통해 Host App으로 전달
- 이하 예제의 “barotalk”은 CFBundleURLSchemes에서 설정한 값으로 변경 필요
```
    objective-c
    ① NSString* scheme = @"bmcxx`x`://verify_vp?appName=barotalk
                            &type=VERIFY
                            &spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ
                            &serviceCode=spdriver
                            &callBackUrl=https://www.saas.go.kr/auth/spnoneprofile/verify
                            &nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460
                            &encryptType=2”;
    ② NSURL *urlScheme = [[NSURL alloc] initWithString:scheme];
    ③ if (@available(iOS 10.0, *)) {
        [[UIApplication sharedApplication] openURL:urlScheme options:@{} completionHandler:^(BOOL success) {
        if (success) { NSLog(@"success"); }
        }]; 
       } else {
          [[UIApplication sharedApplication] openURL:urlScheme];
       }
    1. Host App 에서 검증시 사용할 데이터
    2. URL 형태로 변환
    3. OS 버전 체크 후 Host App 호출
```

```
    Swift
    ① let scheme = "bmc://verify_vp?appName=barotalk
                    &type=VERIFY
                    &spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ
                    &serviceCode=spdriver
                    &callBackUrl=http://1.214.64.23:8081/omniapi/vc/v2/verifyQR
                    &nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460
                    &encryptType=2”
    ② let urlScheme = URL.init(string : scheme)
    ③ if #available(iOS 10.0, *) {
        UIApplication.shared.open(urlScheme, options:[:]){(success) in
         if success {print(“success”)}
       } else {
         UIApplication.shared.openURL(urlScheme)
       }
    1. Host App 에서 검증시 사용할 데이터
    2. URL 형태로 변환
    3. OS 버전 체크 후 Host App 호출
```
- Host App에서 검증 결과를 Client App으로 전달
- 이하 예제의 “barotalk”은 CFBundleURLSchemes에서 설정한 값으로 변경 필요

```
    AppDelegate.m (objective-c) 
    결과 예시) barotalk://verify_vp?result=(boolean)&code=(code)&message=(message)&errorMsg=(errorMsg)
    - (BOOL)application:(UIApplication *)app openURL:(NSURL *)url 
    options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options { 
    NSURLComponents *urlComponents = [NSURLComponents componentsWithURL:url resolvingAgainstBaseURL:NO];
    if (① [urlComponents.host isEqualToString:@"verify_vp"] && urlComponents.queryItems != nil && urlComponents.queryItems.count > 0) {
     NSArray *queryItems = [urlComponents queryItems];
     NSMutableDictionary *dict = [NSMutableDictionary new];
     for (NSURLQueryItem *item in queryItems) {
     [dict setObject:[item value] forKey:[item name]];}
     if (③ [②[dict valueForKey:@"result"] isEqualToString:@"true"]) {
      // Success 처리
     } else {
      // Fail 처리
      }
     }
    }
    1. Host App 에서 검증시 사용할 데이터
    2. URL 형태로 변환
    3. OS 버전 체크 후 Host App 호출
```

```
    AppDelegate.swift (swift)
    결과 예시) barotalk://verify_vp?result=(boolean)&code=(code)&message=(message)&errorMsg=(errorMsg)
    public func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : 
    Any] = [:]) -> Bool {
     let urlComponents = URLComponents(url : url, resolvingAgainstBaseURL:false)!
         if ①let host = urlComponents.host, host == “verify_vp”, let items = urlComponents.queryItems, 
            items.count > 0 {
             if ②let result = items[“result”], ③result.value == “true” {
             // Success 처리
            }
             else{
             // Fail 처리
            }
        }
    }
    1. Host App 에서 검증시 사용할 데이터
    2. URL 형태로 변환
    3. OS 버전 체크 후 Host App 호출
```
#### 나. 검증 결과 조회
- 요청 방식 : POST
- 요청 URI : /mobile/txCheck
- 요청 파라미터

| 파라미터명         | 입력값    | 비고(예시)                        |
|---------------|--------|-----------------------------------|
| nonce         | 랜덤 문자열 | authroize 요청 시 사용했던 값       |
| codeVerifier | 랜덤 문자열 | authroize 요청 시 사용했던 값      |

```
    응답 예시
    {
      "result" : true,
      "code" : 200,
      "message" : "success",
      "errorMsg" : null,
      "txCompleteCode" : "COMPLETE",
      "csrfToken" : null,
      "qrData" : null,
      "qrKey" : "7871d557-2ef4-49d5-b6a5-f30b9df8c0a6",
      "nonce" : "88d15a5d6b543156d98e9db3c51cfa43f98cc65f5b6743f4f41aef9e60b10302",
      "cn" : 로그인 사용자 Cn값
    }
```

#### 다. 로그인
- 요청 방식 : POST
- 요청 URI : /login
- 요청 파라미터

| 파라미터명    | 입력값   | 비고(예시)           |
|----------|-------|------------------|
| username | cn    | 검증 결과 cn값        |
| password | nonce | authroize 요청 시 사용했던 값 |


```
※ 사전 조사
- client_id, client_secret, redirect_uri, post_logout_redirect_uri 등이 연계SaaS DB에 저장된다.
- client_id, client_secret은 이용신청 승인 이후 통합관리포털에서 발급된다. (client_id: 연계서비스ID, client_secret: API Key)
- redirect_uri, post_logout_redirect_uri은 통합관리포털에서 이용신청 시 입력한다. (여러 개 입력 가능, 이용신청 승인 후 수정 가능)
- 잘못된 client_id, client_secret으로 요청하거나 등록되지 않은 redirect_uri를 입력하면 정상적으로 서비스가 제공되지 않음에 유의해야 한다.
```


