package one.ulord.upaas.ucwallet.sdk.test;

import com.alibaba.fastjson.JSONObject;
import one.ulord.upaas.ucwallet.sdk.UCWalletRPCInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Component
public class UCWalletRPCCaller implements UCWalletRPCInterface {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${ucwallet-service.http}")
    private String ucwalletServiceHttp;

    public UCWalletRPCCaller(){
    }

    @Override
    public BigInteger getTransactionCount(String address) {
        JSONObject rv = this.restTemplate.getForEntity(
                ucwalletServiceHttp + "/api/transactionCount/" + address,
                JSONObject.class).getBody();
        if (0 == rv.getInteger("resultCode")){
            return new BigInteger(rv.getString("result"));
        }else{
            throw new RuntimeException("Cannot request ucwallet-service api to get transaction count:"
                    + rv.getString("result"));
        }
    }
}
