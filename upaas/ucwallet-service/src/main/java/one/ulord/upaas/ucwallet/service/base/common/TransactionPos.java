package one.ulord.upaas.ucwallet.service.base.common;

public class TransactionPos {
    private String txHash;
    private int    blockHeight;

    public TransactionPos(String txHash, int blockHeight){
        this.txHash = txHash;
        this.blockHeight = blockHeight;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }
}
