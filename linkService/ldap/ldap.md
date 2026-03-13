## LDAP

### API 목록

| 요청 URL | 메서드 | 응답 형식 | 설명 |
| --- | --- | --- | --- |
| `/api/ldls/userinfo` | GET | JSON | 이름으로 사용자 정보를 조회하는 API (동명이인 시 여러 건 사용자 정보 제공) |
| `/api/ldls/profile` | GET | JSON | 식별값(CN)으로 사용자 정보를 조회하는 API |
| `/api/ldls/org-userlist/{top_instcode}` | GET | JSON | 최상위기관코드로 직원 정보가 포함된 조직도를 제공하는 API |
| `/api/ldls/dept-userlist/{inst_code}` | GET | JSON | 기관코드(부서코드)로 직원 정보가 포함된 조직도를 제공하는 API |
| `/api/ldls/orgchart/{inst_code}` | GET | JSON | 이용기관의 기관코드를 매개변수로 받아 이용기관의 조직도 정보를 제공한다. (직원정보 미포함) <br> ※ 요청한 기관코드 (부서코드)의 하위 부서 정보를 계층 구조로 제공하는 API |
| `/api/ldls/fullorgchart/{inst_code}` | GET | JSON | 기관 코드의 차수 및 서열과 관계없이, 파라미터로 전달한 해당 기관코드의 전체조직도(fullorgchart)를 응답하는 API (직원정보 미포함) |
| `/api/ldls/fullorgchart/{inst_code}/{depth}` | GET | JSON | 기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수(depth)만큼 전체 조직도(fullorgchart)를 응답하는 API(직원정보 미포함) |
| `/api/ldls/which-inst` | GET | JSON | 기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환한다. |
| `/api/ldls-change/` | GET | JSON | 해당 기관에 속한 사용자 정보의 변경 데이터 조회 |
| `/api/ldls-change/top` | GET | JSON | 하위기관에 속한 사용자 정보까지 포함하여 변경 데이터 조회 |
| `/api/ldls-change/detail` | GET | JSON | 해당 기관에 속한 사용자 정보의 변경 상세 데이터 조회 |
| `/api/ldls-change/detail/top` | GET | JSON | 하위기관에 속한 사용자 정보까지 포함하여 변경 상세 데이터 조회 |

---

### 공통 응답 구조

- LDAP 연계 API의 모든 응답은 아래와 같은 공통 구조를 사용합니다.
- 실제 응답 데이터는 `data` 필드에 포함되며, 오류가 발생한 경우 `error` 필드에 오류 정보가 반환됩니다.
- `data` 필드의 세부 구조는 각 API 명세에서 별도로 정의합니다.

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| httpStatusCode | string | HTTP 상태 코드 | - |
| message | string | 응답 메시지 | - |
| cnt | number | 조회된 데이터 개수 | `data` 배열의 길이 |
| data | array | API 요청 결과 데이터 | - |
| error | object \| null | 오류 발생 시 에러 정보 | 정상 응답 시 `null` |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "직원 정보 조회",
  "cnt": 1,
  "data": [
    {
      "instNameAll": "***",
      "cn": "***",
      "empName": "***",
      "position": "",
      "grade": "",
      "eml": "",
      "mbtlnum": "***"
    }
  ],
  "error": null
}
```

---

### API 명세

### (1) 사용자 정보 제공 API

사용자 이름과 사용자가 속한 부서코드를 변수로 받아 전체 조직명, CN, 이름, 직위, 직급, 이메일, 휴대전화번호를 응답합니다. CN, 이메일, 휴대전화번호는 허용데이터 설정에 따라 데이터 제공 여부가 달라집니다.

#### 가. 이름으로 사용자 정보 조회

동명이인 존재 시 여러 건의 사용자 정보가 제공될 수 있습니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/userinfo`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| Name | string | 사용자 이름 | URL 인코딩 필요 |
| InstCode | string | 사용자가 속한 기관코드(부서코드) | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/userinfo
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instNameAll | string | 사용자가 속한 전체 조직명 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| empName | string | 직원 이름 | - |
| position | string \| null | 직위 | - |
| grade | string \| null | 직급 | - |
| email | string \| null | 이메일 | - |
| mbtlnum | string \| null | 휴대전화번호 | - |

#### 나. 식별값(CN)으로 사용자 정보 조회

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/profile`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| Cn | string | 사용자 식별값(CN) | URL 인코딩 필요 |
| InstCode | string | 사용자가 속한 기관코드 | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/profile
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instNameAll | string | 사용자가 속한 전체 조직명 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| empName | string | 직원 이름 | - |
| position | string \| null | 직위 | - |
| grade | string \| null | 직급 | - |
| email | string \| null | 이메일 | - |
| mbtlnum | string \| null | 휴대전화번호 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "직원 정보 조회",
  "cnt": 1,
  "data": [
    {
      "instNameAll": "***",
      "cn": "***",
      "empName": "***",
      "position": "",
      "grade": "***",
      "eml": "",
      "mbtlnum": "***"
    }
  ],
  "error": null
}
```

##### header 인코딩 호출 예시(JAVA)

```java
public class APIRequestExample {
    public static void main(String[] args) {
        try {
            // API 요청 URL 설정
            String urlString = "https://saas.go.kr/api/ldls/userinfo";
            String nameEncoded = URLEncoder.encode(
                "사용자성명",
                StandardCharsets.UTF_8.toString()
            ); // 한글 인코딩

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // 요청 헤더 설정
            conn.setRequestProperty("ApiKey", "복호화API key");
            conn.setRequestProperty("LinkSrvcId", "연계서비스ID");
            conn.setRequestProperty("Name", nameEncoded); // 인코딩한 값으로 헤더 설정
            conn.setRequestProperty("InstCode", "기관코드(부서코드)");

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // 응답 데이터 읽기
            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 출력
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

### (2) 조직도 정보 제공 API - 사용자 정보 포함

#### 가. 최상위기관 단위로 사용자 목록 조회

최상위기관코드를 전달하여 기관에 속한 사용자 목록 전부를 응답합니다. 최상위기관코드 목록은 /api/ldls/which-inst API 호출로 확인할 수 있습니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/org-userlist/{top_instcode}`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| top_instcode | string | 최상위기관코드 | 필수 |
| org_name | string | 검색할 기관명 | 선택 |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/org-userlist/1741000 (최상위기관코드로만 검색) 
GET https://saas.go.kr/api/ldls/org-userlist/1741000?org_name=디지털기반정책과
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instName | string | 기관명 | - |
| instNameAll | string | 전체 기관명 | - |
| odr | number | 차수 | - |
| ord | number | 서열 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| empName | string | 이름 | - |
| position | string \| null | 직위 | - |
| grade | string \| null | 직급 | - |
| eml | string \| null | 이메일 | - |
| mbtlnum | string \| null | 휴대전화번호 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "작성기관의 전체 직원목록 조회",
  "cnt": 12822,
  "data": [
    {
      "instCode": "1741000",
      "instName": "동구",
      "instNameAll": "00광역시 동구",
      "ord": 3,
      "cn": null,
      "empName": "홍길동",
      "position": "",
      "grade": "지방행정서기보",
      "eml": null,
      "mbtlnum": null
    },
    {
      "instCode": "1741000",
      "instName": "동구",
      "instNameAll": "00광역시 동구",
      "ord": 3,
      "cn": null,
      "empName": "홍길동",
      "position": null,
      "grade": "지방행정서기보",
      "eml": null,
      "mbtlnum": null
    }
  ],
  "error": null
}
```

#### 나. 기관코드로 사용자 목록 조회

전달받은 기관코드(부서코드)에 속하는 사용자 목록을 응답합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/dept-userlist/{inst_code}`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| inst_code | string | 기관코드(부서코드) | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/dept-userlist/1741000
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instName | string | 기관명 | - |
| instNameAll | string | 전체 기관명 | - |
| odr | number | 차수 | - |
| ord | number | 서열 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| empName | string | 이름 | - |
| position | string \| null | 직위 | - |
| grade | string \| null | 직급 | - |
| eml | string \| null | 이메일 | - |
| mbtlnum | string \| null | 휴대전화번호 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "부서의 전체 직원목록 조회",
  "cnt": 9,
  "data": [
    {
      "instCode": "1741000",
      "instName": "보건소",
      "instNameAll": "00광역시 동구 보건소",
      "ord": 3,
      "ord2": 22,
      "cn": null,
      "empName": "홍길동",
      "position": "",
      "grade": "지방약무주사보",
      "eml": null,
      "mbtlnum": null
    },
    {
      "instCode": "1741000",
      "instName": "보건소",
      "instNameAll": "00광역시 동구 보건소",
      "ord": 3,
      "ord2": 36,
      "cn": null,
      "empName": "홍길동",
      "position": "보건소장",
      "grade": "지방과학기술서기관(일반임기제)",
      "eml": null,
      "mbtlnum": null
    }
  ],
  "error": null
}
```

---

### (3) 조직도 정보 조회 - 사용자 정보 미포함

#### 가. 하위 조직도 정보 조회

기관코드(부서코드)를 전달하면, 해당 기관의 정보를 포함한 모든 하위 기관의 정보들을 전부 계층 구조로 나타내어 응답합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/orgchart/{inst_code}`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| inst_code | string | 기관코드(부서코드) | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/orgchart/1741000
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instNameAll | string | 전체 기관명 | - |
| instName | string | 기관명 | - |
| odr | string | 차수 | - |
| ord | string | 서열 | - |
| parentInstCode | string | 차상위 기관코드 | - |
| topInstCode | string | 최상위 기관코드 | - |
| childrenOrganizations | array \| null | 하위기관정보 | - |

#### 나. 기관 코드(전체) 조직도 조회 API

기관 코드의 차수(depth) 및 서열과 관계없이, 파라미터로 전달한 해당 기관 코드의 전체 조직도 (fullorgchart)를 응답합니다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만 아니라, 상위 기관 코드들의 정보 또한 역으로 전부 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/fullorgchart/{inst_code}`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| inst_code | string | 기관코드(부서코드) | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/gscs/fullorgchart/1040000
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instNameAll | string | 전체 기관명 | - |
| instName | string | 기관명 | - |
| odr | string | 차수 | - |
| ord | string | 서열 | - |
| parentInstCode | string | 차상위 기관코드 | - |
| topInstCode | string | 최상위 기관코드 | - |
| childrenOrganizations | array \| null | 하위기관정보 | - |

##### 응답 예시

해당 기관 코드의 하위 기관들의 정보뿐만 아니라, 상위 기관 코드들의 정보도 역으로 전부 조회하므로, 응답 결과에는 파라미터로 전달한 기관 코드의 차상위기관 코드가 0인 기관이 가장 최상단의 기관 코드로 조회됩니다. 

```json
{
  "httpStatusCode": "200 OK",
  "message": "00광역시의 전체 조직도 조회",
  "cnt": 672,
  "data": {
    "instCode": "6290000",
    "instNameAll": "00광역시",
    "instName": "00광역시",
    "odr": "1",
    "ord": "006",
    "parentInstCode": "0000000",
    "topInstCode": "6290000",
    "childrenOrganizations": [
      {
        "instCode": "3590000",
        "instNameAll": "00광역시 동구",
        "instName": "동구",
        "odr": "1",
        "ord": "425",
        "parentInstCode": "6290000",
        "topInstCode": "6290000",
        "childrenOrganizations": [
          {
            "instCode": "3590019",
            "instNameAll": "00광역시 동구 보건소",
            "instName": "보건소",
            "odr": "1",
            "ord": "036",
            "parentInstCode": "3590000",
            "topInstCode": "6290000",
            "childrenOrganizations": [
              {
                "instCode": "3590130",
                "instNameAll": "00광역시 동구 보건소 건강정책과",
                "instName": "건강정책과",
                "odr": "4",
                "ord": "057",
                "parentInstCode": "3590019",
                "topInstCode": "6290000",
                "childrenOrganizations": [
                  {
                    "instCode": "3590255",
                    "instNameAll": "00광역시 동구 보건소 건강정책과 치매안심센터",
                    "instName": "치매안심센터",
                    "odr": "5",
                    "ord": "038",
                    "parentInstCode": "3590130",
                    "topInstCode": "6290000"
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  "error": null
}
```

#### 다. 전체 조직도 조회 기관 코드(차수 전달 된) 조직도 조회 API

기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수(depth)만큼 전체 조직도(fullorgchart)를 응답합니다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만 아니라, 상위 기관 코드들의 정보 또한 역으로 전부 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/fullorgchart/{inst_code}/{depth}`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 파라미터

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| inst_code | string | 기관코드(부서코드) | - |
| depth | number | 차수 | - |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/fullorgchart/1741708/4
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instNameAll | string | 전체 기관명 | - |
| instName | string | 기관명 | - |
| odr | string | 차수 | depth에 해당 |
| ord | string | 서열 | - |
| parentInstCode | string | 차상위 기관코드 | - |
| topInstCode | string | 최상위 기관코드 | - |
| childrenOrganizations | array \| null | 하위기관정보 | - |

##### 응답 결과 예시

- 해당 기관 코드와 함께 전달한 `depth`(odr: 차수) 만큼 하위기관 데이터가 조회됩니다.

- /fullorgchart/5670000/2 로 요청 시

```json
{
  "httpStatusCode": "200 OK",
  "message": "경상남도 창원시의 상위 조직 조회",
  "cnt": 91,
  "data": {
    "instCode": "6480000",
    "instNameAll": "경상남도",
    "instName": "경상남도",
    "odr": "1",
    "ord": "115",
    "parentInstCode": "0000000",
    "topInstCode": "6480000",
    "childrenOrganizations": [
      {
        "instCode": "5310000",
        "instNameAll": "경상남도 진주시",
        "instName": "진주시",
        "odr": "2",
        "ord": "429",
        "parentInstCode": "6480000",
        "topInstCode": "6480000"
      },
      {
        "instCode": "5330000",
        "instNameAll": "경상남도 통영시",
        "instName": "통영시",
        "odr": "2",
        "ord": "248",
        "parentInstCode": "6480000",
        "topInstCode": "6480000"
      },
      ...
    ]
  }
}
```

- /fullorgchart/5670000/3 으로 요청 시

```json
{
  "httpStatusCode": "200 OK",
  "message": "경상남도 창원시의 상위 조직 조회",
  "cnt": 968,
  "data": {
    "instCode": "6480000",
    "instNameAll": "경상남도",
    "instName": "경상남도",
    "odr": "1",
    "ord": "115",
    "parentInstCode": "0000000",
    "topInstCode": "6480000",
    "childrenOrganizations": [
      {
        "instCode": "5310000",
        "instNameAll": "경상남도 진주시",
        "instName": "진주시",
        "odr": "2",
        "ord": "429",
        "parentInstCode": "6480000",
        "topInstCode": "6480000",
        "childrenOrganizations": [
          {
            "instCode": "5310028",
            "instNameAll": "경상남도 진주시 의회사무국",
            "instName": "의회사무국",
            "odr": "3",
            "ord": "031",
            "parentInstCode": "5310000",
            "topInstCode": "6480000"
          },
          {
            "instCode": "5310029",
            "instNameAll": "경상남도 진주시 보건소",
            "instName": "보건소",
            "odr": "3",
            "ord": "011",
            "parentInstCode": "5310000",
            "topInstCode": "6480000"
          },
          {
            "instCode": "5310032",
            "instNameAll": "경상남도 진주시 농업기술센터",
            "instName": "농업기술센터",
            "odr": "3",
            "ord": "009",
            "parentInstCode": "5310000",
            "topInstCode": "6480000"
          },
          ...
        ]
      },
      ...
    ]
  },
  "error": null
}
```

#### 라. 기관 코드 최상위기관 목록 조회 API

기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls/which-inst`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls/which-inst
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| instCode | string | 기관코드 | - |
| instNameAll | string | 전체 기관명 | - |
| instName | string | 기관명 | - |
| odr | string | 차수 | - |
| ord | string | 서열 | - |
| parentInstCode | string | 차상위 기관코드 | - |
| topInstCode | string | 최상위 기관코드 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "최상위기관 (21개) 기관 목록 조회",
  "cnt": 219,
  "data": [
    {
      "instCode": "0000001",
      "instNameAll": "대통령",
      "instName": "대통령",
      "odr": "1",
      "ord": "001",
      "parentInstCode": "0000000",
      "topInstCode": "0000001"
    },
    {
      "instCode": "0000002",
      "instNameAll": "국무총리",
      "instName": "국무총리",
      "odr": "1",
      "ord": "008",
      "parentInstCode": "0000000",
      "topInstCode": "0000002"
    },
    {
      "instCode": "0000003",
      "instNameAll": "기획재정부",
      "instName": "기획재정부",
      "odr": "1",
      "ord": "053",
      "parentInstCode": "0000000",
      "topInstCode": "0000003"
    },
    ...
  ]
}
```

---

### (4) 변경 데이터 조회 API

특정 날짜에, 특정 기관에서 변경된 사용자의 정보를 조회하는 API입니다.

#### 가. 기관코드로 조회

해당 기관에 속한 사용자 정보의 변경 데이터를 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls-change`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| InstCode | string | 기관코드 | - |
| Date | string | 조회할 날짜 | `yyyy-MM-dd` 형식 <br> ex) `2025-03-01` |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls-change
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| name | string | 이름 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| email | string \| null | 이메일 | - |
| phoneNumber | string \| null | 휴대전화번호 | - |
| fullName | string | 전체조직명직원명 | - |
| grade | string \| null | 직급 | - |
| position | string \| null | 직위 | - |
| instNameAll | string | 상위기관포함 전체조직명 | - |
| instName | string | 소속기관명 | - |
| topInstCode | string | 최상위기관코드 | - |
| instCode | string | 기관코드 | - |
| secondaryCode | string | 차상위기관코드 | - |
| odr | number | 차수 | - |
| secondaryOdr | number | 차상위기관내조직서열 | - |
| status | string | 변경상태 | 신규: `INS` <br> 업데이트: `UPD` <br> 삭제: `DEL` |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "해당 기관에 속한 사용자 정보의 변경 데이터 조회",
  "cnt": 1,
  "data": [
    {
      "name": "홍길동",
      "cn": null,
      "phoneNumber": null,
      "email": null,
      "fullName": "00광역시 동구 주민복지국 00과 홍길동",
      "grade": null,
      "position": null,
      "instNameAll": "00광역시 동구 주민복지국 00과",
      "instName": "00과",
      "topInstCode": "6290000",
      "instCode": "3590231",
      "secondaryCode": "3590223",
      "odr": 4,
      "secondaryOdr": 15,
      "status": "INS"
    }
  ],
  "error": ""
}
```

#### 나. 최상위기관코드로 조회

하위기관에 속한 사용자 정보까지 포함하여 변경 데이터를 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls-change/top`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| InstCode | string | 기관코드 | - |
| Date | string | 조회할 날짜 | `yyyy-MM-dd` 형식 <br> ex) `2025-03-01` |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls-change/top
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| name | string | 이름 | - |
| cn | string \| null | 사용자 식별값(CN) | - |
| email | string \| null | 이메일 | - |
| phoneNumber | string \| null | 휴대전화번호 | - |
| fullName | string | 전체조직명직원명 | - |
| grade | string \| null | 직급 | - |
| position | string \| null | 직위 | - |
| instNameAll | string | 상위기관 포함 전체조직명 | - |
| instName | string | 소속기관명 | - |
| topInstCode | string | 최상위기관코드 | - |
| instCode | string | 기관코드 | - |
| secondaryCode | string | 차상위기관코드 | - |
| odr | number | 차수 | - |
| secondaryOdr | number | 차상위기관내 조직서열 | - |
| status | string | 변경상태 | 신규: `INS` <br> 업데이트: `UPD` <br> 삭제: `DEL` |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "하기기관에 속한 사용자 정보까지 포함하여 변경 데이터 조회",
  "cnt": 7,
  "data": [
    {
      "name": "홍길동",
      "cn": null,
      "phoneNumber": null,
      "email": null,
      "fullName": "00광역시 동구 주민복지국 통합돌봄과 홍길동",
      "grade": null,
      "position": null,
      "instNameAll": "00광역시 동구 주민복지국 통합돌봄과",
      "instName": "통합돌봄과",
      "topInstCode": "6290000",
      "instCode": "3590231",
      "secondaryCode": "3590223",
      "odr": 4,
      "secondaryOdr": 15,
      "status": "INS"
    },
    {
      "name": "김철수",
      "cn": null,
      "phoneNumber": null,
      "email": null,
      "fullName": "00광역시 동구 도시경관국 보행교통정책과 김철수",
      "grade": "지방행정주사보",
      "position": "",
      "instNameAll": "00광역시 동구 도시경관국 보행교통정책과",
      "instName": "보행교통정책과",
      "topInstCode": "6290000",
      "instCode": "3590242",
      "secondaryCode": "3590225",
      "odr": 4,
      "secondaryOdr": 27,
      "status": "DEL"
    },
    ...
  ]
}
```

#### 다. 변경에 대한 상세 내용을 기관코드로 조회

해당 기관에 속한 사용자 정보의 변경 상세 데이터를 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls-change/detail`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| InstCode | string | 기관코드 | - |
| Date | string | 조회할 날짜 | `yyyy-MM-dd` 형식 <br> ex) `2025-03-01` |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls-change/detail
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| topInstCode | string | 최상위기관코드 | - |
| instCode | string | 기관코드 | - |
| name | string | 이름 | - |
| field | string | 변경항목 | - |
| previousValue | string | 이전값 | - |
| currentValue | string | 현재값 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "해당 기관에 속한 사용자 정보의 변경 상세 데이터 조회",
  "cnt": 2,
  "data": [
    {
      "topInstCode": "6290000",
      "instCode": "6290000",
      "name": "홍길동",
      "cn": null,
      "field": "직위",
      "previousValue": "",
      "currentValue": "육아휴직"
    },
    {
      "topInstCode": "6290000",
      "instCode": "6290000",
      "name": "김철수",
      "cn": null,
      "field": "직위",
      "previousValue": "",
      "currentValue": "질병휴직"
    }
  ],
  "error": ""
}
```

#### 라. 변경에 대한 상세 내용을 최상위기관코드로 조회

하위기관에 속한 사용자 정보까지 포함하여 변경 상세 데이터를 조회합니다.

- 요청 방식: `GET`
- 요청 URI: `/api/ldls-change/detail/top`

##### 요청 헤더

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| ApiKey | string | 통합관리포털에서 확인한 ApiKey 복호화 값 | 암호화키를 이용하여 복호화 |
| LinkSrvcId | string | 통합관리포털에서 확인한 연계서비스 ID | `LKSV2099010119000196` |
| InstCode | string | 기관코드 | - |
| Date | string | 조회할 날짜 | `yyyy-MM-dd` 형식 <br> ex) `2025-03-01` |

##### 요청 URI 예시

```text
GET https://saas.go.kr/api/ldls-change/detail/top
```

##### 응답 항목

| 항목 | 타입 | 설명 | 비고(예시) |
| --- | --- | --- | --- |
| topInstCode | string | 최상위기관코드 | - |
| instCode | string | 기관코드 | - |
| name | string | 이름 | - |
| field | string | 변경항목 | - |
| previousValue | string | 이전값 | - |
| currentValue | string | 현재값 | - |

##### 응답 예시

```json
{
  "httpStatusCode": "200 OK",
  "message": "하위기관에 속한 사용자 정보까지 포함하여 변경 상세 데이터 조회",
  "cnt": 19,
  "data": [
    {
      "topInstCode": "6290000",
      "instCode": "6290000",
      "name": "홍길동",
      "cn": null,
      "field": "직위",
      "previousValue": "",
      "currentValue": "육아휴직"
    },
    {
      "topInstCode": "6290000",
      "instCode": "6290000",
      "name": "김철수",
      "cn": null,
      "field": "직위",
      "previousValue": "",
      "currentValue": "질병휴직"
    },
    {
      "topInstCode": "6290000",
      "instCode": "6290704",
      "name": "심상미",
      "cn": null,
      "field": "직위",
      "previousValue": "팀장",
      "currentValue": "장애인시설팀장"
    },
    ...
  ],
  "error": ""
}
```