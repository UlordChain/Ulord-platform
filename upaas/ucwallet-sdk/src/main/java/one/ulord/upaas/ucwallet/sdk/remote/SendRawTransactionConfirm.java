package one.ulord.upaas.ucwallet.sdk.remote;

public class SendRawTransactionConfirm  extends MQMessage {
    private String txHash;
    private boolean status;

    public SendRawTransactionConfirm(String reqId, String txHash, boolean status){
        super(reqId, MQMessageEnum.CONFIRM);
        this.txHash = txHash;
        this.status = status;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
