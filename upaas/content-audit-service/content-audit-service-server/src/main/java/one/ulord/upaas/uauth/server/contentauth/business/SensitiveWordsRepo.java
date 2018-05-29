/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.server.UPaaSCommandServer;
import one.ulord.upaas.common.sync.SyncOpEnum;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.sync.SyncOpVersionRepo;
import one.ulord.upaas.uauth.common.vo.SensitiveWord;
import one.ulord.upaas.uauth.server.contentauth.services.SensitiveWordService;
import one.ulord.upaas.uauth.server.contentauth.vo.SensitiveWordItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Using a version repo to store violate keywords,
 * a client can get increment records from this repo.
 * @author haibo
 * @since 5/23/18
 */
@Component
public class SensitiveWordsRepo {
    SyncOpVersionRepo<SensitiveWord> sensitiveWordsRepo = new SyncOpVersionRepo<>();

    @Autowired
    UPaaSCommandServer uPaaSCommandServer;
    @Autowired
    SensitiveWordService sensitiveWordService;

    CAuthSyncCommandHandler syncCommandHandler;


    @PostConstruct
    public void init(){
        // Load sensitive words from database
        List<SensitiveWordItem> sensitiveWordItems = sensitiveWordService.loadActive();
        if (sensitiveWordItems != null && sensitiveWordItems.size() > 0){
            List<SyncOpItem<SensitiveWord>> syncOpItems = new ArrayList<>();
            for (SensitiveWordItem sensitiveWordItem : sensitiveWordItems) {
                syncOpItems.add(new SyncOpItem<>(SyncOpEnum.ADD, sensitiveWordItem.of()));
            }
            sensitiveWordsRepo.addRecordOp(syncOpItems);
        }

        syncCommandHandler = new CAuthSyncCommandHandler();
        // register command processor
        uPaaSCommandServer.getServerManager().registerCommandHandler(
                String.valueOf(UPaaSCommandCode.CAUTH_SYNC_TYPE),
                syncCommandHandler);

    }

    public int addSensitiveWords(List<SensitiveWordItem> value){
        if (value == null || value.size() == 0){
            return -1;
        }

        List<SensitiveWord> lstSensitiveWord = new ArrayList<>(value.size());
        int rv;
        int count = 0;
        for (SensitiveWordItem word : value) {
            lstSensitiveWord.add(new SensitiveWord(word.getKeyword(), word.getLevel()));
            rv = sensitiveWordService.createItem(word);
            if (rv == 1){
                count++;
            }
        }
        if (count == value.size()) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD, lstSensitiveWord));
        }
        return count;
    }

    public int addSensitiveWord(SensitiveWordItem value){
        int rv = sensitiveWordService.createItem(value);
        if (rv > 0) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD,
                    new SensitiveWord(value.getKeyword(), value.getLevel())));
        }

        return rv;
    }

    public int removeSensitiveWords(List<SensitiveWordItem> value){
        if (value == null || value.size() == 0){
            return -1;
        }

        List<SensitiveWord> lstSensitiveWord = new ArrayList<>(value.size());
        int count = 0;
        int rv;
        for (SensitiveWordItem word : value) {
            rv = sensitiveWordService.deleteItem(word.getUid());
            if (rv > 0){
                count++;
            }
            lstSensitiveWord.add(new SensitiveWord(word.getKeyword(), word.getLevel()));
        }

        if (count == value.size()) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE, lstSensitiveWord));
        }
        return count;
    }

    public int removeSenstiveWord(SensitiveWordItem value){
        int rv = sensitiveWordService.deleteItem(value.getUid());
        if (rv > 0) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE,
                    new SensitiveWord(value.getKeyword(), value.getLevel())));
        }

        return rv;
    }

    public List<SyncOpItem<SensitiveWord>> getIncrementSensitiveWords(int version){
        return sensitiveWordsRepo.getIncrementRecord(version);
    }

    public List<SensitiveWord> getFullSensitiveWords(){
        return sensitiveWordsRepo.getFullRecord();
    }

    public boolean isNewestVersion(int version){
        return sensitiveWordsRepo.isNewest(version);
    }

    public int getNewestVersion(){
        return sensitiveWordsRepo.getNewestVersion();
    }

    public APIResult retrieve(int pageNum, int pageSize, Map<String,List<String>> criteriaMap) {
        return sensitiveWordService.retrieve(pageNum, pageSize, criteriaMap);
    }
}
