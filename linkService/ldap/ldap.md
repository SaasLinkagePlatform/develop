## LDAP

###  ** 민간 SaaS의 LDAP 연계 개발 단계 이전 [허용데이터 관리](https://github.com/privateSaasOperationSupportCenter/docs/blob/main/2.%20API%20key%20%EB%B0%9C%EA%B8%89%20%EB%B0%8F%20%EC%82%AC%EC%9A%A9.md#5-ldap-%ED%97%88%EC%9A%A9%EB%8D%B0%EC%9D%B4%ED%84%B0-%EA%B4%80%EB%A6%AC-ldap%EB%A5%BC-%EC%97%B0%EA%B3%84%ED%95%98%EB%8A%94-%EA%B2%BD%EC%9A%B0%EC%97%90%EB%A7%8C-%ED%95%B4%EB%8B%B9) 를 확인 바랍니다.

| 요청 URL                                    | 메서드 | 응답 형식 | 설명                                                                                                                     |
|-------------------------------------------|-----|-------|------------------------------------------------------------------------------------------------------------------------|
| /api/ldls/userinfo                        | GET | JSON  | 사용자가 속한 전체 조직명, 성명, 직급, 직위를 제공하는 API (단건 조회)                                                                           |
| /api/ldls/profile                         | GET | JSON  | 사용자가 속한 전체 조직명, 성명, 직급, 직위를 제공하는 API (단건 식별 값 조회)                                                                      |
| /api/ldls/org-userlist/{top_instcode}     | GET | JSON  | 최상위기관코드로 직원 정보가 포함된 조직도를 제공하는 API (기관 단위로 사용자 목록 조회)                                                                   |
| /api/ldls/dept-userlist/{inst_code}       | GET | JSON  | 기관코드(부서코드)로 직원 정보가 포함된 조직도를 제공하는 API (실/국/부서 단위로 사용자 목록 조회)                                                            |
| /api/ldls/orgchart/{inst_code}            | GET | JSON  | 이용기관의 기관코드를 매개변수로 받아 이용기관의 조직도 정보를 제공하는 API (직원정보 미포함) ※ 요청한 기관코드(부서코드)의 하위 부서 정보를 계층구조로 제공                            |
| /api/ldls/fullorgchart/{inst_code}        | GET | JSON  | 기관 코드의 차수 및 서열과 관계없이, 파라미터로 전달한 해당 기관코드의 전체조직도(fullorgchart)를 response하는 API (직원정보 미포함)                                |
| /api/ldls/fullorgchart/{inst_code}/{depth} | GET | JSON  | 기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수(depth)만큼 전체 조직도(fullorgchart)를 response하는 API (직원정보 미포함) |
| /api/ldls/which-inst                      | GET | JSON  | 기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환하는 API                                                               |
| /api/ldls-change/                         | GET | JSON  | 해당 기관에 속한 사용자 정보의 변경 데이터 조회                                                                                                                       |
| /api/ldls-change/top                      | GET | JSON  | 하위기관에 속한 사용자 정보까지 포함하여 변경 데이터 조회                                                                                                                       |
| /api/ldls-change/detail                   | GET | JSON  | 해당 기관에 속한 사용자 정보의 변경 상세 데이터 조회                                                                                                                       |
| /api/ldls-change/detail/top               | GET | JSON  | 하위기관에 속한 사용자 정보까지 포함하여 변경 상세 데이터 조회                                                                                                                       |

---
### (1) 사용자 정보 제공 API
사용자 이름과 사용자가 속한 부서코드를 변수로 받아 전체조직명, 이름, 직급, 직위를 response 한다. (동명이인
존재 시 한 개 이상의 사용자 정보가 제공될 수 있음)
- 요청 방식 : GET
- 요청 URI : /api/ldls/userinfo
- 요청 헤더 

| 이름         | 비고                                                           |
|--------------|----------------------------------------------------------------|
| ApiKey       | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId   | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| Name         | 사용자 이름 `(URL 인코딩 필요)`                                   |
| InstCode     | 사용자가 속한 부서코드(기관코드)                                 |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/userinfo
```

- 요청 방식 : GET
- 요청 URI : /api/ldls/profile
- 요청 헤더

| 이름          | 비고                                              |
|-------------|-------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화)      |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| Cn          | 사용자 Cn `(URL 인코딩 필요)`                                  |
| InstCode    | 사용자가 속한 부서코드(기관코드)                              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/profile
```  

- 사용자 정보 응답 항목 

| 이름          | 비고                      |
|---------------|---------------------------|
| inst_name_all | 사용자가 속한 전체조직명   |
| emp_name      | 사용자 이름                |
| grade         | 사용자 직급                |
| position      | 사용자 직위                |

- header 한글(이름) 인코딩 호출 예시(JAVA)

```java
public class APIRequestExample {
    public static void main(String[] args) {
        try {
            // API 요청 URL 설정
            String urlString = "https://saas.go.kr/api/ldls/userinfo";
            String nameEncoded = URLEncoder.encode("사용자성명", StandardCharsets.UTF_8.
                    toString()); //한글 인코딩
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.
                    getInputStream()));
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
### (2) 조직도 정보 제공 API – 사용자 정보 포함
가. 기관 단위로 사용자 목록 조회
- 최상위기관코드를 전달하여 기관에 속한 사용자 목록 전부를 response 한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/org-userlist/{top_instcode}
  → 최상위기관코드 목록은 /api/ldls/which-inst API 호출로 확인할 수 있다.
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명    | 입력값           | 비고(예시)        |
|---------------|------------------|-------------------|
| top_instcode  | 최상위기관코드    |                   |
| org_name      | 기관명           | 검색할 기관명     |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/org-userlist/1741000 기관코드로만 검색 
  https://saas.go.kr/api/ldls/org-userlist/1741000?org_name=디지털기반정책과
```  

- 응답 항목
  

| 이름          | 비고            |
|---------------|-----------------|
| inst_code     | 기관코드        |
| inst_name_all | 전체 기관명     |
| inst_name     | 기관명          |
| odr           | 차수            |
| ord           | 서열            |
| emp_name      | 사용자 이름     |
| grade         | 사용자 직급     |
| position      | 사용자 직위     |


나. 부서 단위로 사용자 목록 조회
- 전달받은 기관코드(부서코드)에 속하는 사용자 목록을 response 한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/dept-userlist/{inst_code}
- 요청 헤더 

| 이름        | 비고                                                              |
|-------------|-------------------------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)    |

- 요청 파라미터

| 파라미터명  | 입력값           | 비고(예시)       |
|-------------|------------------|------------------|
| inst_code   | 기관코드(부서코드) |                  |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/dept-userlist/1741000
```  

- 응답 항목
  

| 이름          | 비고            |
|---------------|-----------------|
| inst_code     | 기관코드        |
| inst_name_all | 전체 기관명     |
| inst_name     | 기관명          |
| odr           | 차수            |
| ord           | 서열            |
| emp_name      | 사용자 이름     |
| grade         | 사용자 직급     |
| position      | 사용자 직위     |


---

### (3) 조직도 정보 조회 – 사용자 정보 미포함
가. 하위 조직도 정보 조회
- 기관 코드를 전달하면, 해당 기관의 정보를 포함한 모든 하위 기관의 정보들을 전부 Hierarchy 구조로 나타내어
response 한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/orgchart/{inst_code}
- 요청 헤더 

| 이름        | 비고                                                              |
|-------------|-------------------------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)    |

- 요청 파라미터

| 파라미터명  | 입력값    | 비고(예시)   |
|-------------|-----------|--------------|
| inst_code   | 기관코드  |              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/orgchart/1741000
```  

- 응답 항목

| 이름              | 비고                |
|-------------------|---------------------|
| inst_code         | 기관코드            |
| inst_name_all     | 전체 기관명         |
| inst_name         | 기관명              |
| odr               | 차수                |
| ord               | 서열                |
| parent_inst_code  | 차상위 기관코드     |
| top_inst_code     | 최상위 기관코드     |

나. 기관 코드 (전체) 조직도 조회 API
- 기관 코드의 차수(depth) 및 서열과 관계없이, 파라미터로 전달한 해당 기관 코드의 전체 조
직도(fullorgchart)를 response 해준다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만
아니라, 상위 기관 코드들의 정보 또한 역으로 전부 조회한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/fullorgchart/{inst_code}
- 요청 헤더 

| 이름        | 비고                                                              |
|-------------|-------------------------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)    |

- 요청 파라미터

| 파라미터명  | 입력값    | 비고(예시)   |
|-------------|-----------|--------------|
| inst_code   | 기관코드  |              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/fullorgchart/1741842
```  

- 응답 항목

| 이름                  | 비고                    |
|-----------------------|-------------------------|
| instCode              | 기관코드                |
| instNameAll           | 전체 기관명             |
| instName              | 기관명                  |
| odr                   | 차수                    |
| ord                   | 서열                    |
| parentInstCode        | 차상위 기관코드         |
| topInstCode           | 최상위 기관코드         |
| childrenOrganizations | 하위기관정보            |


- 응답 결과 예시
  - 해당 기관 코드의 하위 기관들의 정보뿐만아니라, 상위 기관 코드들의 정보도 역으로 전부 조회하므로, 응답 결과
  에는 파라미터로 전달한 기관 코드의 차상위 기관 코드가 0인 기관이 가장 최상단의 기관 코드로 조회된다.


다. 전체 조직도 조회 기관 코드 (차수 전달 된) 조직도 조회 API
- 기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수
(depth)만큼 전체 조직도(fullorgchart)를 response 해준다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만
아니라, 상위 기관 코드들의 정보 또한 역으로 전부 조회한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/fullorgchart/{inst_code}/{depth}
- 요청 헤더

| 이름        | 비고                                                              |
|-------------|-------------------------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)    |

- 요청 파라미터

| 파라미터명   | 입력값    | 비고(예시)   |
|--------------|-----------|--------------|
| inst_code    | 기관코드  |              |
| depth        | 차수      |              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/fullorgchart/1741842/4
```  

- 응답 항목

| 이름                  | 비고                    |
|-----------------------|-------------------------|
| instCode              | 기관코드                |
| instNameAll           | 전체 기관명             |
| instName              | 기관명                  |
| odr                   | 차수                    |
| ord                   | 서열                    |
| parentInstCode        | 차상위 기관코드         |
| topInstCode           | 최상위 기관코드         |
| childrenOrganizations | 하위기관정보            |

- 응답 결과 예시
  - 해당 기관 코드의 하위 기관들의 정보뿐만아니라, 상위 기관 코드들의 정보도 역으로 전부 조회하므로, 응답 결과
  에는 해당 기관 코드와 파라미터로 함께 전달한 depth(=차수, json 데이터에서는 odr로 표현됨) 만큼 데이터가
  조회된다. 


라. 기관 코드 최상위기관 목록 조회 API
- 기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환한다.
- 요청 방식 : GET
- 요청 URI : /api/ldls/which-inst
- 요청 헤더

| 이름        | 비고                                                              |
|-------------|-------------------------------------------------------------------|
| ApiKey      | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId  | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)    |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls/which-inst
```  

- 응답 항목

| 이름              | 비고                |
|-------------------|---------------------|
| instCode          | 기관코드            |
| instNameAll       | 전체 기관명         |
| instName          | 기관명              |
| odr               | 차수                |
| ord               | 서열                |
| parentInstCode    | 차상위 기관코드     |
| topInstCode       | 최상위 기관코드     |

### (4) 변경 데이터 조회 API

특정 날짜에, 특정 기관에서 변경된 사용자의 정보를 조회하는 API 

#### 가. 기관코드로 조회

해당 기관에 속한 사용자 정보의 변경 데이터 조회

- 요청 방식 : GET
- 요청 URI : /api/ldls-change
- 요청 헤더

| 이름         | 비고                                           |
|------------|----------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화)   |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| InstCode   | 기관코드                            |
| Date       | 조회할 날짜 <br/>yyyy-MM-dd 형식으로 ex) 2025-03-01        |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls-change
```
- 응답 항목

| 이름                | 비고                                   |
|-------------------|--------------------------------------|
| name              | 이름                                   |
| email             | 이메일                                  |
| phoneNumber       | 휴대전화번호                               |
| fullName          | 전체조직명직원명                             |
| grade             | 직급                                   |
| position          | 직위                                   |
| instNameAll       | 상위기관포함 전체조직명                         |
| instName          | 소속기관명                                |
| topInstCode       | 최상위기관코드                              |
| instCode          | 기관코드                                 |
| secondaryCode     | 차상위기관코드                              |
| odr               | 차수                                   |
| secondaryOdr      | 차상위기관내조직서열                           |
| status            | 변경상태<br/> 신규: INS, 업데이트: UPD, 삭제 : DEL |


#### 나. 최상위기관코드로 조회

하위기관에 속한 사용자 정보까지 포함하여 변경 데이터 조회

- 요청 방식 : GET
- 요청 URI : /api/ldls-change/top
- 요청 헤더

| 이름         | 비고                                           |
|------------|----------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화)   |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| InstCode   | 기관코드                             |
| Date       | 조회할 날짜 <br/>yyyy-MM-dd 형식으로 ex) 2025-03-01   |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls-change/top
```

- 응답 항목

| 이름                | 비고                                   |
|-------------------|--------------------------------------|
| name              | 이름                                   |
| email             | 이메일                                  |
| phoneNumber       | 휴대전화번호                               |
| fullName          | 전체조직명직원명                             |
| grade             | 직급                                   |
| position          | 직위                                   |
| instNameAll       | 상위기관포함 전체조직명                         |
| instName          | 소속기관명                                |
| topInstCode       | 최상위기관코드                              |
| instCode          | 기관코드                                 |
| secondaryCode     | 차상위기관코드                              |
| odr               | 차수                                   |
| secondaryOdr      | 차상위기관내조직서열                           |
| status            | 변경상태<br/> 신규: INS, 업데이트: UPD, 삭제 : DEL |

#### 다. 변경에 대한 상세 내용을 기관코드로 조회

해당 기관에 속한 사용자 정보의 변경 상세 데이터 조회

- 요청 방식 : GET
- 요청 URI : /api/ldls-change/detail
- 요청 헤더

| 이름         | 비고                                           |
|------------|----------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화)   |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| InstCode   | 기관코드                            |
| Date       | 조회할 날짜 <br/>yyyy-MM-dd 형식으로 ex) 2025-03-01        |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls-change
```
- 응답 항목

| 이름           | 비고      |
|--------------|---------|
| topInstCode  | 최상위기관코드 |
| instCode     | 기관코드    |
| name         | 이름      |
| field        | 변경항목    |
| previousValue | 이전값     |
| currentValue | 현재값     |

#### 라. 변경에 대한 상세 내용을 최상위기관코드로 조회

하위기관에 속한 사용자 정보까지 포함하여 변경 상세 데이터 조회

- 요청 방식 : GET
- 요청 URI : /api/ldls-change/detail/top
- 요청 헤더

| 이름         | 비고                                           |
|------------|----------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화)   |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196) |
| InstCode   | 기관코드                             |
| Date       | 조회할 날짜 <br/>yyyy-MM-dd 형식으로 ex) 2025-03-01   |

```
  <요청 URI 예시>
  https://saas.go.kr/api/ldls-change/top
```

- 응답 항목

| 이름           | 비고      |
|--------------|---------|
| topInstCode  | 최상위기관코드 |
| instCode     | 기관코드    |
| name         | 이름      |
| field        | 변경항목    |
| previousValue | 이전값     |
| currentValue | 현재값     |