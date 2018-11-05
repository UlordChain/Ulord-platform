package one.ulord.upaas.ucwallet.service.base.common;

public class SendRawTransactionResponse  extends MQMessage{
    private String txHash;

    public SendRawTransactionResponse(String reqId, String txHash){
        super(reqId, MQMessageEnum.RESPONSE);
        this.txHash = txHash;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxhash(String txHash) {
        this.txHash = txHash;
    }
}
