package one.ulord.upaas.ucwallet.sdk.remote;

public class SendRawTransactionRequest extends MQMessage{
    private String rawTransaction;

    public SendRawTransactionRequest(){}
    public SendRawTransactionRequest(String reqId, String rawTransaction){
        super(reqId, MQMessageEnum.REQUEST);
        this.rawTransaction = rawTransaction;
    }

    public String getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(String rawTransaction) {
        this.rawTransaction = rawTransaction;
    }
}
