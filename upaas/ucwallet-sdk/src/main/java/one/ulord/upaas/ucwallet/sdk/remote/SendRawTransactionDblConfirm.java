package one.ulord.upaas.ucwallet.sdk.remote;

public class SendRawTransactionDblConfirm extends MQMessage{
    private String txHash;
    private int confirmBlocks;

    public SendRawTransactionDblConfirm(String reqId, String txHash, int confirmBlocks){
        super(reqId, MQMessageEnum.DBLCONFIRM);
        this.txHash = txHash;
        this.confirmBlocks = confirmBlocks;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getConfirmBlocks() {
        return confirmBlocks;
    }

    public void setConfirmBlocks(int confirmBlocks) {
        this.confirmBlocks = confirmBlocks;
    }
}
