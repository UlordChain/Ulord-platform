/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.sync;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Version Repo, which can store a add/delete operation in the repo.
 * You can get full record(after merge all add/delete operation)
 * or a increment record(after merge add/delete operation)
 * <pre>
 * versionRepo----+
 *            |
 * +---------------------------+
 * | X0   X1   X2   ...   Xn   |
 * +------|--------------------+
 *        v
 * {version, OpItems}
 *               |
 *               v
 *    +---------------------+
 *    | Op0  Op1  Op2  Op3  |
 *    +-------|-------------+
 *            v
 *         {op, E}
 * </pre>
 * @see #addRecordOp(List)
 * @see #getFullRecord()
 * @see #getIncrementRecord(int)
 * @author haibo
 * @since 5/23/18
 */
@Slf4j
public class SyncOpVersionRepo<E> {
    public static int MAX_KEEP_HISTORY_LEN = 100;
    public static int MIN_MERGE_COUNT = 10;

    /**
     * A versionRepo of Element operation version versionRepo, each item is a version operate.
     * A new version will be insert into the head of list
     */
    List<SyncOpVersion<E>> versionRepo = new LinkedList<>();
    AtomicInteger currentVersion = new AtomicInteger(0);

    /**
     * check a version is or not the newest version
     * @param version newest version
     * @return true version is a newest version, false - not a newest version,
     * it version big that current newest version, a {@link SyncVersionException} will be thrown.
     */
    public boolean isNewest(int version){
        int localVersion = getNewestVersion();
        if (localVersion == -1){
            return true;
        }else{
            if (localVersion == version){
                return true;
            }else if (localVersion > version){
                return false;
            }else{
                throw new SyncVersionException("Version big that current max version, full sync be need.");
            }
        }
    }

    /**
     * Get newest version
     * @return version, -1 indicated there is nothing in repo
     */
    public int getNewestVersion(){
        SyncOpVersion<E> syncOpVersion = versionRepo.get(0);
        if (syncOpVersion == null){
            return -1;
        }else{
           return syncOpVersion.getVersion();
        }
    }

    /**
     * Get full record from versionRepo records
     * @return a valid items
     */
    public List<E> getFullRecord(){
        List<E> results = new ArrayList<>();
        SyncOpVersion<E> syncOpVersion = versionRepo.get(0);
        if (syncOpVersion == null){
            return results;
        }
        // merge all versionRepo
        mergeFrom(syncOpVersion.getVersion(), results);

        return results;
    }

    /**
     * Get an increment records from versionRepo cache
     * @param version a specified version
     * @return
     */
    public List<SyncOpItem<E>> getIncrementRecord(int version){
        if (versionRepo.size() == 0){ // There is no any record
            throw new SyncVersionException("No record in version repo.");
        }
        // check version
        if (versionRepo.get(0).getVersion() < version){
            throw new SyncVersionException("Version bigger that current max version, full sync be need.");
        }

        SyncOpVersion<E> lastVer = versionRepo.get(versionRepo.size() - 1);
        if (lastVer.getVersion() > version){
            // using the oldest version instead
            throw new SyncVersionException("Version too old, full sync be need.");
        }
        // merge to the newest version
        List<SyncOpItem<E>> results = new ArrayList<>();

        // locate version position
        int offset = versionRepo.size() - (version - lastVer.getVersion()) - 1;
        results.addAll(versionRepo.get(offset).getItems());
        for (int i = offset - 1; i >= 0; i--){
            if (versionRepo.get(i).getVersion() < version){
                break;
            }
            mergeOpVersion(results, versionRepo.get(i));
        }
        return results;
    }

    /**
     * Add record into version repo
     * @param opItems a operation collection
     */
    public synchronized void addRecordOp(List<SyncOpItem<E>> opItems){
        int version = currentVersion.getAndIncrement();
        SyncOpVersion<E> opVersion = new SyncOpVersion<>(version, opItems);
        versionRepo.add(0, opVersion);

        if (versionRepo.size() >= MAX_KEEP_HISTORY_LEN){
            // get oldest versionRepo
            List<SyncOpItem<E>> baseOpItems = new ArrayList<>();
            int lastVersion = 0;
            for (int i = 0; i <= MIN_MERGE_COUNT; i++){
                // merge up
                SyncOpVersion<E> lastValue = versionRepo.remove(versionRepo.size() - 1);
                lastVersion = lastValue.getVersion();
                mergeOpVersion(baseOpItems, lastValue);
            }
            // set to tail
            versionRepo.add(versionRepo.size(), new SyncOpVersion<>(lastVersion, baseOpItems));
        }
    }

    /**
     * Wrap sync op time from a normal list
     * @param op Sync operation
     * @param items a normal list
     * @return
     */
    public List<SyncOpItem<E>> wrapSyncOpItem(SyncOpEnum op, List<E> items){
        List<SyncOpItem<E>> opItems = new ArrayList<>();
        items.forEach(item -> {
            opItems.add(new SyncOpItem<>(op, item));
        });

        return opItems;
    }

    /**
     * Wrap sync op time from an item
     * @param op Sync operation
     * @param item a normal item
     * @return
     */
    public List<SyncOpItem<E>> wrapSyncOpItem(SyncOpEnum op, E item){
        List<SyncOpItem<E>> opItems = new ArrayList<>();
        opItems.add(new SyncOpItem<>(op, item));

        return opItems;
    }

    private List<E> mergeFrom(int version, List<E> results){
        int e = versionRepo.get(versionRepo.size()-1).getVersion();
        int s = version - e;
        for (int i = s; i >= 0; i--) {
            mergeVersion(results, versionRepo.get(i));
        }

        return results;
    }

    private List<E> mergeVersion(List<E> results, SyncOpVersion<E> value){
        value.getItems().forEach(item -> {
            switch(item.getOp()){
                case ADD:
                    results.add(item.getData());
                    break;
                case DELETE:
                    results.remove(item.getData());
            }
        });

        return results;
    }

    private List<SyncOpItem<E>> mergeOpVersion(List<SyncOpItem<E>> opItems, SyncOpVersion<E> value){
        log.debug("Merge version:{}", value.getVersion());
        value.getItems().forEach(item -> {
            switch(item.getOp()){
                case ADD:
                    opItems.add(item);
                    break;
                case DELETE:
                    boolean rv = opItems.remove(new SyncOpItem<>(SyncOpEnum.ADD, item.getData()));
                    if (!rv){
                        // current item does not exist current version tree, we need to keep it
                        opItems.add(item);
                    }
            }
        });

        return opItems;
    }
}
