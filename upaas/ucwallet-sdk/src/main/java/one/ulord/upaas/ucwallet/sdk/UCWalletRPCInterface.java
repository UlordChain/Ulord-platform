package one.ulord.upaas.ucwallet.sdk;

import java.math.BigInteger;

public interface UCWalletRPCInterface {
    BigInteger getTransactionCount(String address);
}
