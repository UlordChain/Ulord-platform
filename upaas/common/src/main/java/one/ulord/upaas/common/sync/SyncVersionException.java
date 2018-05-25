/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.sync;

/**
 * Sync version exception
 * @author haibo
 * @since 5/23/18
 */
public class SyncVersionException extends RuntimeException {
    public SyncVersionException(String msg){
        super(msg);
    }
}
