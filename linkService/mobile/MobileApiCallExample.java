package linkService.mobile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MobileApiCallExample {
    public void apiCall() throws URISyntaxException, IOException, InterruptedException {

        // HttpClient 생성
        HttpClient client = HttpClient.newHttpClient();

        //HttpRequest 생성
        HttpRequest request = HttpRequest.newBuilder()
                //ex) 모바일공무원인증 API 요청 URI 예시 (추가예정)
                .uri(new URI(""))
                .header("Content-Type","application/json")

                /*
                 *
                 *
                 *
                 *
                 */
                .build();

        // 요청을 보내고 응답을 받음
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 상태 코드 출력
        System.out.println("Response Code: " + response.statusCode());

        // 응답 본문 출력
        System.out.println("Response Body: " + response.body());
    }
}
