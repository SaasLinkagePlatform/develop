## 모바일공무원인증 연계 API

### 개요

- 브라우저에 민간SaaS 로그인이 되어 있으면, 다른 민간 SaaS에서는 로그인이 불필요합니다. (단, 동일 브라우저 내 탭에서만 가능)
- 이용기관에서 적용하는 정책(지원하는 디바이스, 지원하는 브라우저 등) 에 따라서 모바일 로그인 기능 App To App 이나 Web To App 을 적용할 수 있습니다.
- 개발용 서버: `https://www.saas.go.kr/auth-stg`
  - 테스트 공무원증이 있거나 개발 시 사용
- 운영용 서버: `https://www.saas.go.kr/auth`
  - 테스트 공무원증 사용 불가

### API 목록

| 요청 URL | 메서드 | 응답 형식 | 설명 |
| --- | --- | --- | --- |
| `/oauth2/authorize` | GET | JSON | 연계 API에게 권한 인증을 요청하는 API |
| `/oauth2/token` | POST | JSON | 연계 API에 Access Token / Refresh Token을 요청하는 API |
| `/oauth2/userinfo` | GET | JSON | 로그인한 사용자 정보를 요청하는 API |
| `/oauth2/connect/logout` | GET | JSON | 로그인한 사용자의 세션을 만료시키는 API |
| `/mobile/txCheck` | POST | JSON | App to App 검증 완료 여부를 확인하는 API |
| `/login` | POST | JSON | App to App 검증 후 로그인을 요청하는 API |

***[에러코드](#에러-코드)는 문서 최하단에 있습니다.***

---

### API 명세

### (1) 로그인

#### 가. 인가 코드 요청

- 요청 방식: `GET`
- 요청 URI: `/oauth2/authorize`

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| response_type | string | code | 고정값 |
| scope | string | openid | 고정값 |
| client_id | string | 지정한 client_id | 연계서비스 ID (ex: `LKSV2099010119000196`) |
| redirect_uri | string | 지정한 redirect_uri | ex) `http://testsaas.com/oauth/callback` |
| state | string | 랜덤 문자열 | - |
| nonce | string | 랜덤 문자열 | - |
| web_login | boolean | true / false | 기본 로그인의 경우 `true`, <br> App to App 로그인의 경우 `false` |
| code_challenge | string | 랜덤 문자열 | `code_verifier`의 해시 값 (App to App 사용 시) |
| code_challenge_method | string | S256 | 고정값 (App to App 사용 시) |

\*\*고정값: 문자 그대로 입력(ex: code, openid, S256)

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

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Content-Type | `application/x-www-form-urlencoded` | 고정값 |

##### 요청 본문

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| grant_type | string | `authorization_code` | 고정값 |
| client_id | string | 지정한 client_id | 연계서비스 ID (ex: `LKSV2099010119000196`) |
| client_secret | string | 지정한 client_secret | API 키 |
| redirect_uri | string | 지정한 redirect_uri | ex) `http://testsaas.com/oauth/callback` |
| code | string | 발급 받은 code | 토큰 연장 할 땐 사용 안 함 |
| code_verifier | string | 랜덤 문자열 | App to App 요청 시 사용 |

\*\*고정값: 문자 그대로 입력(ex: authorization_code)

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| access_token | string | API 요청 인증에 사용 | - |
| refresh_token | string | Access Token 갱신에 사용 | - |
| id_token | string | 로그아웃 시 사용 | - |
| scope | string | `openid` | 고정값 |
| token_type | string | `Bearer` | 고정값 |
| expires_in | Integer | Access Token 유효기간(초) | `3000` |

##### 사후 처리

1) 응답받은 `access_token`, `refresh_token`, `id_token` 값을 Cookie, Local Storage 등에 저장합니다.
2) `access_token`을 사용하여 로그인한 사용자 정보를 조회합니다.

#### 다. 사용자 정보 조회

- 요청 방식: `GET`
- 요청 URI: `/oauth2/userinfo`

##### 요청 헤더

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Authorization | `Bearer ${ACCESS_TOKEN}` | - |

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| cn | string | 사용자 식별값 | - |
| name | string | 사용자 이름 | - |
| instCode | string | 사용자가 속한 부서코드 | - |

---

### (2) Token 갱신

인증을 위한 Access Token의 유효기간은 1시간으로, Access Token이 만료되면 갱신요청을 하여 Token을 갱신할 수 있습니다.

- 요청 방식: `POST`
- 요청 URI: `/oauth2/token`

##### 요청 헤더

| 항목 | 설명 | 비고(예시) |
| --- | --- | --- |
| Content-Type | `application/x-www-form-urlencoded` | - |

##### 요청 본문

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| grant_type | string | `refresh_token` | 고정값 |
| client_id | string | 지정한 client_id | 연계서비스 ID <br> ex) LKSV2099010119000196 |
| client_secret | string | 지정한 client_secret | API 키 |
| refresh_token | string | 저장한 refresh_token | - |

\*\* 고정값: 문자 그대로 입력(ex: authorization_code)

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| access_token | string | API 요청 인증에 사용 | - |
| refresh_token | string | Access Token 갱신에 사용 | - |
| id_token | string | 로그아웃 시 사용 | - |
| scope | string | `openid`로 고정 | 고정값 |
| token_type | string | `Bearer`로 고정 | 고정값 |
| expires_in | Integer | 3000 (Access Token 유효기간(s)) | - |

##### 사후 처리

- 갱신받은 `access_token`, `refresh_token`, `id_token` 값을 Cookie, Local Storage 등에 저장합니다.

---

### (3) 로그아웃

- 요청 방식: `GET`
- 요청 URI: `/oauth2/connect/logout`

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| id_token_hint | string | 저장된 id_token | - |
| post_logout_redirect_uri | string | 지정한 redirect_uri | 예: `http://testsaas.com` |

##### 사후 처리

- 로그아웃에 성공하면 지정한 post_logout_redirect_uri 페이지로 이동되며, 저장된 access_token, refresh_token, id_token을 삭제합니다.

---

### (4) App to App

#### 가. 구현 가이드

- 모바일 공무원증 App to App 가이드는 Mobile Web Browser에서 모바일 공무원증 Application 호출하는 방법만 안내합니다.
- CSS, javascript, Angular, React, Vue.js 등의 browser에서 발생하는 오류는 기술 지원이 어려우므로 양해 부탁드립니다.

#### 1) Android

##### 1. Host App 호출 정보

| Client App 설정 정보 | 입력값 |
| --- | --- |
| Client App에서 사용할 Scheme 값 | `Bmc` |
| Client App에서 사용할 Action 값 | `kr.go.id.bmc.VERIFY_VP` |

##### 2. App to App 연동 Flow

- 로그인 요청 후 응답받은 결과값을 사전 정의된 Scheme를 통해 Host App으로 전달합니다.
- `appName = barotalk`은 Client App에서 정의된 앱 이름을 의미합니다.

```java
// (1) Activity 전역에 런처 클래스 선언
ActivityResultLauncher<Intent> resultLauncher;

// (2) onCreate() 실행 시 런처 등록
resultLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    activityResultCallback);

// (3) Host App 에서 검증시 사용할 데이타
Uri uri = Uri.parse("bmc://verify_vp?appName=barotalk
    &type=VERIFY(고정값)
    &spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ
    &serviceCode=spdriver(민간 SaaS 서비스 코드)
    &callBackUrl=https://www.saas.go.kr/auth/spnoneprofile/verify(고정값으로 전달됨)
    &nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460
    &encryptType=2(고정값)"
);

// (4) Host App AndroidManifest.xml 에 정의된 Action 값.
Intent intent = new Intent("kr.go.id.bmc.VERIFY_VP", uri);

// (5) Host App 호출
resultLauncher.launch(intent);
```

- Host App에서 검증 결과를 Client App으로 전달합니다.

```java
// 결과 예시: bmc://verify_vp?result=true&code=200&message=success&errorMsg=
ActivityResultCallback<ActivityResult> activityResultCallback =
    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            int resultCode = result.getResultCode();
            if (resultCode == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // (1) Host App 에서 결과값으로 전달한 파라미터
                    String strResult = data.getData().getQueryParameter("result");

                    // (2) Client App 에서 결과 처리
                    if ("true".equals(strResult)) {
                        Log.i("TAG", "success");
                    } else {
                        Log.i("TAG", "fail");
                    }
                }
            }
        }
    };
```

#### (2) iOS

##### 1. `info.plist`에 URL Types 정의

- 유니크한 Identifier값을 설정합니다. URL Schemes에 불리는 앱의 scheme을 설정합니다.

```xml
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

- `canOpenURL` 메소드를 사용하여 앱 호출가능 여부를 체크할 경우
- `info.plist`에 `LSApplicationQueriesSchemes` 배열에 해당 앱을 부를 앱의 scheme을 추가 canOpenURL을 호출하지 않고 바로 openURL을 사용하여도 무방합니다.

##### 2. 연동 Flow

- 로그인 요청 응답 결과 값을 사전 정의된 Scheme를 통해 Host App으로 전달합니다.
- 아래 예제의 `barotalk`은 `CFBundleURLSchemes`에 설정한 값으로 변경해야 합니다.

```objc
// (1) Host App 에서 검증시 사용할 데이터
NSString *scheme = 
@"bmc://verify_vp?appName=barotalk"
@"&type=VERIFY"
@"&spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ"
@"&serviceCode=spdriver"
@"&callBackUrl=https://www.saas.go.kr/auth/spnoneprofile/verify"
@"&nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460"
@"&encryptType=2";

// (2) URL 형태로 변환
NSURL *urlScheme = [[NSURL alloc] initWithString:scheme];

// (3) OS 버전 체크 후 Host App 호출
if (@available(iOS 10.0, *)) {
    [[UIApplication sharedApplication] openURL:urlScheme options:@{} completionHandler:^(BOOL success) {
        if (success) { NSLog(@"success"); }
    }];
} else {
    [[UIApplication sharedApplication] openURL:urlScheme];
}
```

```swift
// (1) Host App 에서 검증시 사용할 데이터
let scheme = 
"bmc://verify_vp?appName=barotalk" +
"&type=VERIFY" +
"&spDid=did:omn:3eYAoCcxgbVNNTxeSQXeo6Y3MbhQ" +
"&serviceCode=spdriver" +
"&callBackUrl=http://1.214.64.23:8081/omniapi/vc/v2/verifyQR" +
"&nonce=988cef69d625e4db508b6b78151cabf59ad63da386a0d2f2b5e269037478f460" +
"&encryptType=2"

// (2) URL 형태로 변환
let urlScheme = URL.init(string: scheme)

// (3) OS 버전 체크 후 Host App 호출
if #available(iOS 10.0, *) {
    UIApplication.shared.open(urlScheme, options: [:]) { (success) in
        if success { print("success") }
    }
} else {
    UIApplication.shared.openURL(urlScheme)
}
```

- Host App에서 검증 결과를 Client App으로 전달합니다.
- 이하 예제의 `barotalk`은 `CFBundleURLSchemes`에서 설정한 값으로 변경이 필요합니다.

```objc
// 결과 예시: barotalk://verify_vp?result=(boolean)&code=(code)&message=(message)&errorMsg=(errorMsg)
- (BOOL)application:(UIApplication *)app
            openURL:(NSURL *)url
            options:(NSDictionary<UIApplicationOpenURLOptionsKey, id> *)options {
    NSURLComponents *urlComponents =
        [NSURLComponents componentsWithURL:url resolvingAgainstBaseURL:NO];

    // (1) Host App에서 검증시 사용할 데이터
    if ([urlComponents.host isEqualToString:@"verify_vp"] &&
        urlComponents.queryItems != nil &&
        urlComponents.queryItems.count > 0) {
        NSArray *queryItems = [urlComponents queryItems];
        NSMutableDictionary *dict = [NSMutableDictionary new];

        for (NSURLQueryItem *item in queryItems) {
            [dict setObject:[item value] forKey:[item name]];
        }

        // (2) URL 형태로 변환
        // (3) OS 버전 체크 후 Host App 호출
        if ([[dict valueForKey:@"result"] isEqualToString:@"true"]) {
            // Success 처리
        } else {
            // Fail 처리
        }
    }
}
```

```swift
// 결과 예시: barotalk://verify_vp?result=(boolean)&code=(code)&message=(message)&errorMsg=(errorMsg)
public func application(
    _ app: UIApplication,
    open url: URL,
    options: [UIApplication.OpenURLOptionsKey : Any] = [:]
) -> Bool {
    let urlComponents = URLComponents(url: url, resolvingAgainstBaseURL: false)!

    // (1) Host App에서 검증시 사용할 데이터
    if let host = urlComponents.host,
       host == "verify_vp",
       let items = urlComponents.queryItems,
       items.count > 0 {
        // (2) URL 형태로 변환
        // (3) OS 버전 체크 후 Host App 호출
        if let result = items.first(where: { $0.name == "result" }),
           result.value == "true" {
            // Success 처리
        } else {
            // Fail 처리
        }
    }
}
```

#### 나. 검증 결과 조회

- 요청 방식: `POST`
- 요청 URI: `/mobile/txCheck`

##### 요청 헤더

| 파라미터명 | 타입 | 입력값 | 비고(예시) |
| --- | --- | --- | --- |
| nonce | string | 랜덤 문자열 | authorize 요청 시 사용했던 값 |
| codeVerifier | string | 랜덤 문자열 | authorize 요청 시 사용했던 값 |

#### 다. 로그인

- 요청 방식: `POST`
- 요청 URI: `/login`

##### 요청 헤더

| 파라미터명 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- |
| username | string | `cn` | 검증 결과의 `cn` 값 |
| password | string | `nonce` | authorize 요청 시 사용했던 값 |

---

## 에러 코드

| 코드 | 에러 메시지 | 상태코드 |
| --- | --- | --- |
| MP10001 | 시스템 장애로 통신이 원활하지 않습니다 | 500 |
| MP10002 | 잘못된 요청입니다 | 400 |
| MP10003 | 요청하신 데이터가 없습니다 | 404 |
| MP10004 | 등록된 Client ID가 없습니다 | 404 |
| MP10005 | 등록된 ApiKey가 없습니다 | 404 |
| MP10006 | 등록된 서비스가 없습니다 | 404 |
| MP10007 | 지원하지 않는 형식입니다 | 405 |
| MP20001 | 인증되지 않았습니다 | 401 |
| MP20002 | 접근 권한이 없습니다 | 403 |
| MP20003 | 요청에 필요한 항목이나 값이 잘못되었습니다 | 400 |
| MP20004 | OAuth2 인증 흐름을 통한 접근이 아닙니다 | 401 |
| MP30001 | 잘못된 토큰 형식입니다 | 400 |
| MP30002 | 유효하지 않은 토큰입니다 | 401 |
| MP30003 | 유효하지 않은 API키입니다 | 401 |
| MP30004 | 유효하지 않은 접근입니다 | 401 |
| MP30005 | 만료된 인증서 요청입니다 | 401 |

```text
※ 사전 조사
- client_id, client_secret, redirect_uri, post_logout_redirect_uri 등은 연계 SaaS DB에 저장됩니다.
- client_id, client_secret은 이용 신청 승인 이후 통합관리포털에서 발급됩니다.
  (client_id: 연계서비스 ID, client_secret: API Key)
- redirect_uri, post_logout_redirect_uri는 통합관리포털에서 이용 신청 시 입력합니다.
  (여러 개 입력 가능, 이용 신청 승인 후 수정 가능)
- 잘못된 client_id, client_secret으로 요청하거나 등록되지 않은 redirect_uri를 입력하면 정상적으로 서비스가 제공되지 않을 수 있습니다.
```
