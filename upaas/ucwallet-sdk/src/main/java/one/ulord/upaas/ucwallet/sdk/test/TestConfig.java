package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.ContentContract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;

import java.io.IOException;

@Configuration
public class TestConfig {
    @Value("${ucwallet-sdk.test.token-address}")
    private String tokenAddress;

    @Value("${ucwallet-sdk.test.center-publish-address}")
    private String publicAddress;

    @Value("${ucwallet-sdk.test.multransfer-address}")
    private String mulTransferAddress;

    @Value("${ucwallet-sdk.test.keystore-file}")
    private String keystoreFile;

    @Value("${ucwallet-sdk.test.keystore-password}")
    private String keystorePassword;

    @Bean
    ContentContract contentContract(UCWalletRPCCaller ucWalletRPCCaller) throws IOException, CipherException {
            ContentContract contentContract = new ContentContract(ucWalletRPCCaller, tokenAddress, mulTransferAddress,
                    publicAddress, keystoreFile, keystorePassword);
            return contentContract;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(3000);
        return factory;
    }
}
