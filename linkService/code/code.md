## CODE

| 요청 URL                                   | 메서드 | 응답 형식 | 설명                                                                                           |
|--------------------------------------------|--------|-----------|------------------------------------------------------------------------------------------------|
| /api/gscs/get-allcdspcs                    | GET    | JSON      | 행정표준코드와 연계된 모든 코드종의 종류를 코드종명과 코드종으로 제공함                        |
| /api/gscs/data/{code_name}                 | GET    | JSON      | 코드종을 통해 해당 코드종에 포함되어 있는 상세 코드 목록을 제공함. {codeName} 값은 행정표준 코드 연계 상황을 참고 |
| /api/gscs/which-inst                       | GET    | JSON      | 기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환함         |
| /api/gscs/{inst_code}                      | GET    | JSON      | 기관 코드 값을 파라미터로 전달하여, 해당 기관 코드의 모든 하위 노드들을 전부 Hierarchy 구조로 반환함 |
| /api/gscs/{inst_code}/{depth}              | GET    | JSON      | 기관 코드를 전달하면서, 하위 기관의 파라미터로 전달된 숫자만큼 하위 기관들의 차수(depth)만큼 데이터를 조회 후 Hierarchy 구조로 나타내어 response함 |
| /api/gscs/fullorgchart/{inst_code}         | GET    | JSON      | 기관 코드의 차수 및 서열과 관계없이, 파라미터로 전달한 해당 기관코드의 전체조직도(fullorgchart)를 response해줌 |
| /api/gscs/fullorgchart/{inst_code}/{depth} | GET    | JSON      | 기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수(depth)만큼 전체 조직도(fullorgchart)를 response해줌 |

---
### (1) 행정표준코드 234종 코드종명 조회 API
행정표준코드관리시스템에서 제공하는 234종의 행정표준코드의 “코드종명”과 해당 코드종명의 “코드종 영문명”
을 제공한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/get-allcdspcs
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/get-allcdspcs
```  

- 응답 항목

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| korean_nm     | 코드종명 |
| cd_spcs | 코드종 영문명   |
---

### (2) 행정표준코드 상세 코드 목록 조회 API
(영문) 코드종 이름을 매개변수로 받아 코드종에 해당하는 데이터를 반환합니다.
- 요청 방식 : GET
- 요청 URI (1) : /api/gscs/data/{code_name}
  (페이징 정보 미전달시)
- 요청 URI (2) : /api/gscs/data/{code_name}?page={page}&size={size}
  (페이징 정보 전달시)

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명    | 입력값       | 비고(예시)            |
|---------------|--------------|-----------------------|
| code_name     | 코드종명     | ex. 기관, 직종, 등     |
| page          | 페이지      | 응답 받을 페이지 수   |
| size          | 크기         | 응답 받을 데이터 사이즈 |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/data/{3. 행정표준코드 연계 상황 中 택1}
  
  ※ 예) “기관” 코드 요청 시,
  https://saas.go.kr/api/gscs/data/inst ●요청 URI (1) 또는
  https://saas.go.kr/api/gscs/data/inst?page=1&size=100 ●요청 URI (2) 으로 요청
  페이징 정보를 제공하는 page 및 size 정보는 필수값이 아닌 선택값이며, query string 형식으로 파라미터 값을 전달함
```  

※ 참고
기관 코드의 경우, 대량데이터를 Json 데이터로 변환하는 과정에서 많은 시간이 소요될 수 있으므로 API 데이터 테스트 시 Open source API
https://hoppscotch.io/ 를 사용할 것을 추천함.

- 응답 항목
  - 가. 페이징 정보를 파라미터로 전달하는 경우
    
    | 이름   | 비고                             |
    |--------|----------------------------------|
    | data   | 행정표준코드에서 제공하는 데이터들 |

  - 나. 페이징 정보를 파라미터로 전달하는 경우
  
    | 이름     | 비고                                             |
    |----------|--------------------------------------------------|
    | pageInfo | 해당 행정표준코드 조회 리스트의 page, lastPage, dataSize 정보 |
    | data     | 행정표준코드에서 제공하는 데이터들               |

---

### (3) 기관 코드 최상위기관 목록 조회 API
기관 코드 중 차상위기관 코드의 값이 0이고 차수가 1인 최상위기관의 목록을 추출하여 반환한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/which-inst
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/which-inst
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
---

### (4) 기관 코드 (전체) 하위 노드 조회 API
기관 코드를 전달하여, 해당 기관의 정보를 포함한 모든 하위 기관의 정보들을 전부 Hierarchy 구조로 나타내어
response 한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/{inst_code}
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명  | 입력값           | 비고(예시)       |
|-------------|------------------|------------------|
| inst_code   | 기관코드(부서코드) |                  |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/1741823
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

---

### (5) 기관 코드 (차수 전달 된) 하위 노드 조회 API
기관 코드를 전달하면서, 하위 기관의 파라미터로 전달된 숫자만큼 하위 기관들의 차수. 즉, depth만큼 데이터를
조회한 후 Hierarchy 구조로 나타내어 response 한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/{inst_code}/{depth}
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명   | 입력값    | 비고(예시)   |
|--------------|-----------|--------------|
| inst_code    | 기관코드  |              |
| depth        | 차수      |              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/1741823/2
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

- 응답 항목
  - 응답 결과에는 해당 기관 코드와 파라미터로 함께 전달한 depth (=차수, json 데이터에서는 odr로 표현됨) 만큼
  데이터가 조회된다. 

---
### (6) 기관 코드 (전체) 조직도 조회 API
기관 코드의 차수(depth) 및 서열과 관계없이, 파라미터로 전달한 해당 기관 코드의 전체 조직도(fullorgchart)를
response 해준다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만 아니라, 상위 기관 코드들의 정보 또한 역으
로 전부 조회한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/fullorgchart/{inst_code}
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명  | 입력값           | 비고(예시)       |
|-------------|------------------|------------------|
| inst_code   | 기관코드(부서코드) |                  |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/fullorgchart/1741000
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

---
### (7) 기관 코드 (차수 전달 된) 조직도 조회 API
기관 코드의 차수 및 서열과 관련하여, 파라미터로 해당 기관 코드를 전달했을 때 함께 전달하는 파라미터의 차수
(depth)만큼 전체 조직도(fullorgchart)를 response 해준다. 이 경우, 해당 기관 코드의 하위 기관들의 정보뿐만
아니라, 상위 기관 코드들의 정보 또한 역으로 전부 조회한다.
- 요청 방식 : GET
- 요청 URI : /api/gscs/fullorgchart/{inst_code}/{depth}
- 요청 헤더

| 이름       | 비고                                                                 |
|------------|----------------------------------------------------------------------|
| ApiKey     | 통합관리포털에서 확인한 ApiKey 복호화 값 (암호화키를 이용하여 복호화) |
| LinkSrvcId | 통합관리포털에서 확인한 연계서비스ID (ex: LKSV2099010119000196)      |

- 요청 파라미터

| 파라미터명   | 입력값    | 비고(예시)   |
|--------------|-----------|--------------|
| inst_code    | 기관코드  |              |
| depth        | 차수      |              |

```
  <요청 URI 예시>
  https://saas.go.kr/api/gscs/fullorgchart/1741842/3
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

- 응답 항목
  - 해당 기관 코드의 하위 기관들의 정보뿐만아니라, 상위 기관 코드들의 정보도 역으로 전부 조회하므로, 응답 결과
  에는 해당 기관 코드와 파라미터로 함께 전달한 depth(=차수, json 데이터에서는 odr로 표현됨) 만큼 데이터가
  조회된다.


---
#### [참고] 행정표준코드 연계 상황 [코드종명 분류ID 234종]

| 행정표준코드 분류명       | 행정표준코드 분류ID       |
|--------------------------|---------------------------|
| 1. 기관                   | inst                      |
| 2. 법정동                 | standong                  |
| 3. 외국명                 | forignname                |
| 4. 국가자격면허           | qualication               |
| 5. 공무원구분             | civilservent              |
| 6. 직종                   | occupation                |
| 7. 직종세분류             | occuclassify              |
| 8. 직군                   | jobgroup                  |
| 9. 직렬                   | jobseries                 |
| 10. 직류                  | jobsubseries              |
| 11. 계급                  | jobclass                  |
| 12. 직급                  | jobrank                   |
| 13. 직위                  | jobposition               |
| 14. 임용구분              | publicexam                |
| 15. 외국어                 | foriginlang               |
| 16. 공무원교육훈련        | edutrain                  |
| 17. 근무처구분            | workspace                 |
| 18. 학력                  | edulevel                  |
| 19. 학위                  | degree                    |
| 20. 건축물용도            | bildprps                  |
| 21. 학과                  | subject                   |
| 22. 세입                  | anlrve                    |
| 23. 취득등록세구분        | acqstax                   |
| 24. 농약                  | pesticide                 |
| 25. 항공기종류            | arplain                   |
| 26. 식품원재료            | rawmtrl                   |
| 27. 식품안전품목분류      | foodsafe                  |
| 28. 식품안전시험항목      | safetest                  |
| 29. 지방세부가국세        | llxtax                    |
| 30. 공무원교육훈련과정    | edutrainctr               |
| 31. 포상종류              | rward                     |
| 32. 혈액형                | blood                     |
| 33. 외국어능력            | foriegnablty              |
| 34. 고과반영여부          | performyn                 |
| 35. 국외여행목적          | nationtraprp             |
| 36. 국외여행비용부담      | nationtracost             |
| 37. 가옥(주거형태)        | residence                 |
| 38. 종교                  | religion                  |
| 39. 가족관계              | family                    |
| 40. 호봉승급구분          | salaryup                  |
| 41. 호봉                  | paylevel                  |
| 42. 징계종류              | unishment                 |
| 43. 비위유형              | misconduct                |
| 44. 보훈대상자            | rwdtrp                    |
| 45. 공문서보존기간        | veterans                  |
| 46. 지목                  | pointout                  |
| 47. 건설기계종류          | conduckind                |
| 48. 장애등급              | severgrad                 |
| 49. 의료보험보호구분      | mediensurance             |
| 50. 언론매체              | media                     |
| 51. 법인과주주와의관계    | coandshrel                |
| 52. 지역개발세물건구분    | localdevtax               |
| 53. 송달구분              | delvyse                   |
| 54. 징수결의구분          | levyse                    |
| 55. 징수부처리구분        | levyprcse                 |
| 56. 체납관리구분          | arrearmng                 |
| 57. 등록세대사처리결과    | regitaxrslt               |
| 58. 지방세중과감면코드    | llxredtax                 |
| 59. 선박용도              | shipprs                   |
| 60. 선박종류              | shipkind                  |
| 61. 시설물,부수시설물     | fcty                      |
| 62. 담배소매인구분        | tobasell                  |
| 63. 면허세종별            | licentax                  |
| 64. 납세의무자(개인,법인) | taxpayer                  |
| 65. 건물용도              | buldprs                   |
| 66. 토지(임야)대장구분     | landse                    |
| 67. 축척구분              | scse                      |
| 68. 토지이동사유          | landmvrson                |
| 69. 토지농지등급구분      | landfarm                  |
| 70. 등급변동구분          | grademv                   |
| 71. 지적공부구분          | publandler                |
| 72. 토지이동종목구분      | landmvtiem                |
| 73. 신청구분              | apply                     |
| 74. 수수료구분            | feese                     |
| 75. 토지이동정리구분      | landmvorg                 |
| 76. 결번사유구분          | missnum                   |
| 77. 시행신고구분          | enfreport                 |
| 78. 소유구분              | possess                   |
| 79. 소유권변동원인구분    | ownmvrson                 |
| 80. 소유권변경정리구분    | ownchange                 |
| 81. 비법인등록대장변동사유구분  | ledgerchance               |
| 82. 등록사항처리구분           | regmattprcs               |
| 83. 집합건물전유부분폐쇄구분    | buldclsg                   |
| 84. 집합건물자료구분           | buildata                   |
| 85. 관광자원                   | sightsee                   |
| 86. 관광안내소                 | tourinfo                   |
| 87. 관광객이용편의시설         | tourfacl                   |
| 88. 농림수산시설              | agiseafac                  |
| 89. 광물종류                   | mineral                    |
| 90. 승계구분                   | succession                 |
| 91. 허가사항                   | permission                 |
| 92. 연료구분                   | fuel                       |
| 93. 시장개설허가업종           | mrktopenprm                |
| 94. 보건의료기관               | healthins                  |
| 95. 환자상태                   | pattstts                   |
| 96. 공중위생업종               | pubhltsec                  |
| 97. 기초생활수급권자           | livelihood                 |
| 98. 공사종류                   | constrtype                 |
| 99. 저당사항                   | mortgage                   |
| 100. 도시계획지구              | cvilregion                 |
| 101. 기사유형                  | reportendabce              |
| 102. 보도성향                  | nscincln                   |
| 103. 주민등록표등록구분(거주상태) | resiregistr                |
| 104. 토지형태구분(종합토지세)  | landtype                   |
| 105. 결손사유                  | defctrson                  |
| 106. 고지결정                  | infodesc                   |
| 107. 공시송달사유              | pubnotirson                |
| 108. 과세구분                  | taxation                   |
| 109. 레저세과세대상            | lestax                     |
| 110. 담배소비세과세대상        | tobatax                    |
| 111. 과오납금미환부사유        | ovpayrson                  |
| 112. 과오납미환부금처리사유    | ovpayamtprcs               |
| 113. 과오납및감액사유          | ovpaydecrerson             |
| 114. 기분                      | quarter                    |
| 115. 납부유형                  | paytype                    |
| 116. 도시계획세분류            | cityplan                   |
| 117. 건설기계등록구분          | consmachine                |
| 118. 말소구분                  | cancellaion                |
| 119. 반송고지사유              | returnotirson              |
| 120. 범위구분                  | scope                      |
| 121. 부과구분                  | levy                       |
| 122. 세목(지방세분야)          | taxitem                    |
| 123. 세분류                    | detailtax                  |
| 124. 수납구분                  | receive                    |
| 125. 수납처리구분              | receiveprcs                |
| 126. 신구구분                  | newold                     |
| 127. 압류구분                  | seizure                    |
| 128. 압류물건                  | seizurething               |
| 129. 압류설정사유             | seizurerson                |
| 130. 압류진행                  | seizureprgrs               |
| 131. 압류해제사유             | seizureoff                 |
| 132. 유무구분                  | existence                  |
| 133. 자동이체신청결과         | autopayrslt                |
| 134. 자동이체출금처리결과     | autopaywithdrw            |
| 135. 자동차세납부구분         | cartax                     |
| 136. 수납자료구분             | stordata                   |
| 137. 제증명구분               | proof                      |
| 138. 업소구분                 | business                   |
| 139. 주민세특별징수대상       | residentax                 |
| 140. 사업소세납세자관리       | businesstax                |
| 141. 집합건물동               | builddong                  |
| 142. 집합건물호수             | buildnum                   |
| 143. 징수유예사유             | reprieverson               |
| 144. 징수유예종류             | reprievekind               |
| 145. 징수유예취소사유         | reprievecanc               |
| 146. 차량용도구분             | vhcleseuse                 |
| 147. 차량출처구분             | vhclesourc                 |
| 148. 차종                      | vhcletype                  |
| 149. 징수처분구분             | taxcollect                 |
| 150. 체납사유                 | arrearson                  |
| 151. 체납유형                 | arreartype                 |
| 152. 체납정리                 | arreararng                 |
| 153. 충당취소구분             | suplecanc                  |
| 154. 일반공사사업비용도       | genprjcost                 |
| 155. 건축구분                 | build                      |
| 156. 건축주구분               | buildown                   |
| 157. 건축행위구분             | buildact                   |
| 158. 설계도서의구분           | desse                      |
| 159. 인공구조물구분           | artistrc                   |
| 160. 부대시설구분             | subfac                     |
| 161. 복리시설의구분           | welfac                     |
| 162. 건축물대장의변동원인      | bildcause                  |
| 163. 해상구역                  | seazone                    |
| 164. 문화재종류                | culkind                    |
| 165. 피해구분                  | damge                      |
| 166. 피해대상                  | dmgtrgt                    |
| 167. 재난유형                  | disatype                   |
| 168. 이재민수용시설           | disafclty                  |
| 169. 인명피해변동              | casualties                 |
| 170. 여권유형                  | passport                   |
| 171. 차량말소등록구분          | vhclecanc                  |
| 172. 교통사업자유형            | tfbuisman                  |
| 173. 지적소유권변동형태구분     | intellright                |
| 174. 자동차이전등록구분        | carbefore                  |
| 175. 차량압류등록구분          | vhcleseiz                  |
| 176. 주택유형구분              | housetype                  |
| 177. 원점구분                  | site                       |
| 178. 선거유형구분              | electype                   |
| 179. 건축물구조ID              | buldid                     |
| 180. 계약방법                  | ctrtmthd                   |
| 181. 직무등급구분              | jobgrade                   |
| 182. 국적취득유형              | nlacqtype                  |
| 183. 특수지등급                | spgrade                    |
| 184. 재직상태                  | hdofstts                   |
| 185. 가산경력                  | addexpr                    |
| 186. 경과                      | corel                      |
| 187. 전문특기분야              | poldep                     |
| 188. 출장                      | businesstrip               |
| 189. 위탁훈련                  | cnsgntrn                   |
| 190. 근거법령                  | basislaw                   |
| 191. 감사조치집행단계          | auditstep                  |
| 192. 측량오류                  | mesrerr                    |
| 193. 공적분야                  | pbrealm                    |
| 194. 지붕구조구분              | roofstruc                  |
| 195. 지하지상구분              | grandlocate                |
| 196. 도로종류구분              | roadtype                   |
| 197. 주차장유형                | paringlot                  |
| 198. 공시지가(계획시설)유형   | offilandprice              |
| 199. 사업시행종목구분          | prjimple                   |
| 200. 경력구분                  | career                     |
| 201. 급여구분                 | salary                     |
| 202. 급여신분변동유형         | salstat                    |
| 203. 재직휴직구분             | offtype                    |
| 204. 가입단체성격구분         | joincharct                 |
| 205. 평정비대상사유구분       | balratio                   |
| 206. 색맹구분                 | colorblind                 |
| 207. 취득세/농특세과세구분    | acqagstax                  |
| 208. 특수지                   | spcladdr                   |
| 209. 회계                     | account                    |
| 210. 기여금구분               | contribute                 |
| 211. 자연재해저감시설        | disasredcfac               |
| 212. 결제방법부호             | paymthd                    |
| 213. 인도조건부호             | delismbol                  |
| 214. 운송형태구분부호         | transtype                  |
| 215. 신체등급구분             | bodydegr                   |
| 216. 수산업종분류             | fishindus                  |
| 217. 심급구분                 | instdegree                 |
| 218. 소송구분                 | lawsuit                    |
| 219. 의결결과구분             | decresult                  |
| 220. 석유판매업구분           | oilsell                    |
| 221. 승급제한사유구분         | promtrson                  |
| 222. 통근수단구분             | commute                    |
| 223. 휴가구분                 | vacation                   |
| 224. 액화석유가스허가업종    | lpgpermit                  |
| 225. 부재사유유형             | abstype                    |
| 226. 결손취소사유             | defcancrson                |
| 227. 건축관계자구분           | bildinterper               |
| 228. 주부속건축물구분         | mainbuld                   |
| 229. 지번표시유형             | lotnoty                    |
| 230. 층구분                   | floor                      |
| 231. 오수정화시설처리방식    | sewmthd                    |
| 232. 오수정화시설용량단위    | sewunit                    |
| 233. 건축물대장종류           | bildregkind                |
| 234. 용도지역지구구분        | spcfcse                    |
