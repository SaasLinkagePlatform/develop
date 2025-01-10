package linkService.apiCallExample;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCallExample {

    public void apiCall() throws URISyntaxException, IOException, InterruptedException {

        // HttpClient 생성
        HttpClient client = HttpClient.newHttpClient();

        //HttpRequest 생성
        HttpRequest request = HttpRequest.newBuilder()
                //ex) 행정표준코드 API 요청 URI 예시 -> "혈액형" 코드 정보 API 호출
                .uri(new URI("https://saas.go.kr/api/gscs/data/blood"))
                .header("Content-Type","application/json")

                /* 아래의 LinkSrvcId, ApiKey 의 value 에 발급받은 연계서비스ID 및 복호화키를 setting 하여 Main 실행
                 *
                 * 테스트키 - httpStatusCode 상태 코드를 통해 민간 SaaS 연계 서비스와의 통신 여부 (통신 성공 - 200 OK) 확인 가능
                 * 실제키 - 테스트키 인증 성공 이후 실제키로 API 호출 시, 실데이터를 json 형태로 반환
                 *
                 */
                .setHeader("LinkSrvcId", "your-LinkSrvcId")
                .setHeader("ApiKey", "your-ApiKey")
                .build();

        // 요청을 보내고 응답을 받음
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 상태 코드 출력
        System.out.println("Response Code: " + response.statusCode());

        // 응답 본문 출력
        System.out.println("Response Body: " + response.body());
    }
}
