/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.sync;

import lombok.Data;

import java.util.List;

/**
 * @author haibo
 * @since 5/23/18
 */
@Data
public class SyncOpVersion<E> {
    private List<SyncOpItem<E>> items;
    private int version;

    public SyncOpVersion(int version, List<SyncOpItem<E>> value){
        this.version = version;
        this.items = value;
    }
}
