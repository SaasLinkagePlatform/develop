package linkService.apiCallExample;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    /** 전자정부 연계 SaaS 서비스의 실제 API 호출 예시 코드는
     * privateSaasOperationSupportCenter 하위 develop repo의 linkService/apiCallExample 샘플 코드 참고
     * @param args
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        ApiCallExample call = new ApiCallExample();
        call.apiCall();
    }
}
