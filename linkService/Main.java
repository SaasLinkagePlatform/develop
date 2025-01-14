package linkService;

import linkService.code.CodeApiCallExample;
import linkService.gpki.GpkiApiCallExample;
import linkService.ldap.LdapApiCallExample;
import linkService.mobile.MobileApiCallExample;

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
        //행정표준코드
        CodeApiCallExample code = new CodeApiCallExample();
        code.apiCall();

        //LDAP
        LdapApiCallExample ldap = new LdapApiCallExample();
        ldap.apiCall();

        //모바일공무원인증
        MobileApiCallExample mobile = new MobileApiCallExample();
        mobile.apiCall();

        //GPKI
        GpkiApiCallExample gpki = new GpkiApiCallExample();
        gpki.apiCall();
    }
}
