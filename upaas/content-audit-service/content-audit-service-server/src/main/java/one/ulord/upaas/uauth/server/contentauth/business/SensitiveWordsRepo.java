/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.server.UPaaSCommandServer;
import one.ulord.upaas.common.sync.SyncOpEnum;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.sync.SyncOpVersionRepo;
import one.ulord.upaas.uauth.common.vo.SensitiveWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Using a version repo to store violate keywords,
 * a client can get increment records from this repo.
 * @author haibo
 * @since 5/23/18
 */
@Component
public class SensitiveWordsRepo {
    SyncOpVersionRepo<SensitiveWords> sensitiveWordsRepo = new SyncOpVersionRepo<>();

    @Autowired
    UPaaSCommandServer uPaaSCommandServer;

    CAuthSyncCommandHandler syncCommandHandler;


    @PostConstruct
    public void init(){
        // Load sensitive words from database

        syncCommandHandler = new CAuthSyncCommandHandler();
        // register command processer
        uPaaSCommandServer.getServerManager().registerCommandHandler(
                String.valueOf(UPaaSCommandCode.CAUTH_SYNC_TYPE),
                syncCommandHandler);

    }

    public void addSensitiveWords(List<SensitiveWords> value){
        sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD, value));
    }

    public void addSenstiveWord(SensitiveWords value){
        sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD, value));
    }

    public void removeSensitiveWords(List<SensitiveWords> value){
        sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE, value));
    }

    public void removeSenstiveWord(SensitiveWords value){
        sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE, value));
    }

    public List<SyncOpItem<SensitiveWords>> getIncrementSensitiveWords(int version){
        return sensitiveWordsRepo.getIncrementRecord(version);
    }

    public List<SensitiveWords> getFullSensitiveWords(){
        return sensitiveWordsRepo.getFullRecord();
    }

    public boolean isNewestVersion(int version){
        return sensitiveWordsRepo.isNewest(version);
    }

    public int getNewestVersion(){
        return sensitiveWordsRepo.getNewestVersion();
    }
}
