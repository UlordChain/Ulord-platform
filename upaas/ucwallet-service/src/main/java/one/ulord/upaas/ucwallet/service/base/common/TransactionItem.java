package one.ulord.upaas.ucwallet.service.base.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TransactionItem extends MQMessage{
    private String rawTransaction;
    private String from;
    private String routingKey;
    private BigInteger nonce;
    private BigDecimal gasFee;
    private int retryCnt = 0;
    private int blockHeight;

    public TransactionItem(String reqId, String routingKey, String from,
                           String rawTransaction, BigInteger nonce, BigDecimal gasFee,
                           int retryCnt, int blockHeight){
        super(reqId, MQMessageEnum.TXITEM);
        this.routingKey = routingKey;
        this.from = from;
        this.rawTransaction = rawTransaction;
        this.nonce = nonce;
        this.gasFee = gasFee;
        this.retryCnt = retryCnt;
        this.blockHeight = blockHeight;
    }

    public String getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTx(String rawTx) {
        this.rawTransaction = rawTx;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getRetryCnt() {
        return retryCnt;
    }

    public void setRetryCnt(int retryCnt) {
        this.retryCnt = retryCnt;
    }

    public void incRetryCnt(){this.retryCnt++;}

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public BigDecimal getGasFee() {
        return gasFee;
    }

    public void setGasFee(BigDecimal gasFee) {
        this.gasFee = gasFee;
    }

    @Override
    public String toString() {
        return "TransactionItem{" +
                "rawTransaction='" + rawTransaction + '\'' +
                ", from='" + from + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", nonce=" + nonce +
                ", gasFee=" + gasFee +
                ", retryCnt=" + retryCnt +
                ", blockHeight=" + blockHeight +
                '}';
    }
}
