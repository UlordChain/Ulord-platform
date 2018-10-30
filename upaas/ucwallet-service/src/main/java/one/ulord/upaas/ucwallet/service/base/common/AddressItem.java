package one.ulord.upaas.ucwallet.service.base.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AddressItem {
    private long updateTimestamp;
    private BigDecimal balance;
    private BigInteger minNonce;
    private BigInteger maxNonce;

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigInteger getMaxNonce() {
        return maxNonce;
    }

    public void setMaxNonce(BigInteger maxNonce) {
        this.maxNonce = maxNonce;
    }

    public BigInteger getMinNonce() {
        return minNonce;
    }

    public void setMinNonce(BigInteger minNonce) {
        this.minNonce = minNonce;
    }
}
