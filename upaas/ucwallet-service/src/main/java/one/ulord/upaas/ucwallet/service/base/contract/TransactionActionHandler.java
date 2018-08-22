/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.contract;

/**
 * @author haibo
 * @since 7/5/18
 */
public interface TransactionActionHandler {
    void success(String id, String txhash);
    void fail(String id, String message);
}
