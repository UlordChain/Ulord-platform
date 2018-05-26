/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.sync;

import lombok.Data;

/**
 * @author haibo
 * @since 5/23/18
 */
@Data
public class SyncOpItem<E> {
    E data;
    SyncOpEnum op;

    public SyncOpItem(SyncOpEnum op, E value) {
        this.op = op;
        this.data = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SyncOpItem)) {
            return false;
        }

        SyncOpItem<E> o = convertInstanceOfObject(obj, this.getClass());
        if (this.op == o.op && this.data.equals(o.getData())){
            return true;
        }

        return false;
    }

    private static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch(ClassCastException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return 17 + 31 * data.hashCode() + 31 * op.hashCode();
    }
}
