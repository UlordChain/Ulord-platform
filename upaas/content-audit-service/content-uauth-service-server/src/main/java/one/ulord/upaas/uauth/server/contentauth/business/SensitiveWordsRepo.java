/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.server.UPaaSCommandServer;
import one.ulord.upaas.common.sync.SyncOpEnum;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.sync.SyncOpVersionRepo;
import one.ulord.upaas.uauth.common.vo.SensitiveWord;
import one.ulord.upaas.uauth.common.vo.UAuthSyncRequest;
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
    CAuthSyncSubscriber cAuthSyncSubscriber;


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

        cAuthSyncSubscriber = new CAuthSyncSubscriber(); // create a new subscriber manager
        syncCommandHandler = new CAuthSyncCommandHandler(this, cAuthSyncSubscriber);
        // register command processor
        uPaaSCommandServer.getServerManager().registerCommandHandler(
                String.valueOf(UPaaSCommandCode.CAUTH_SYNC_TYPE.getValue()),
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
            lstSensitiveWord.add(new SensitiveWord(word.getKeyword(), word.getLevel(), word.getScene()));
            rv = sensitiveWordService.createItem(word);
            if (rv == 1){
                count++;
            }
        }
        if (count == value.size()) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD, lstSensitiveWord));
            updateClients();
        }
        return count;
    }

    private void updateClients() {
        cAuthSyncSubscriber.broadcastMessage(buildVersionUpdateRequest());
    }

    private BaseMessage buildVersionUpdateRequest() {
        BaseMessage message = new BaseMessage();
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.CAUTH_SYNC_VER_CHG_NOTIFY);

        UAuthSyncRequest request = new UAuthSyncRequest();
        request.setVersion(sensitiveWordsRepo.getNewestVersion());
        message.setObject(request);

        return message;
    }

    public int addSensitiveWord(SensitiveWordItem value){
        int rv = sensitiveWordService.createItem(value);
        if (rv > 0) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.ADD,
                    new SensitiveWord(value.getKeyword(), value.getLevel(), value.getScene())));
            updateClients();
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
            if (word.getUid() == 0){
                continue;
            }
            rv = sensitiveWordService.deleteItem(word.getUid());
            if (rv > 0){
                count++;
            }
            lstSensitiveWord.add(new SensitiveWord(word.getKeyword(), word.getLevel(), word.getScene()));
        }

        if (count == value.size()) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE, lstSensitiveWord));
            updateClients();
        }
        return count;
    }

    public int removeSensitiveWord(SensitiveWordItem value){
        int rv = sensitiveWordService.deleteItem(value.getUid());
        if (rv > 0) {
            sensitiveWordsRepo.addRecordOp(sensitiveWordsRepo.wrapSyncOpItem(SyncOpEnum.DELETE,
                    new SensitiveWord(value.getKeyword(), value.getLevel(), value.getScene())));
            updateClients();
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
    public boolean isNeedFullSync(int version){
        return sensitiveWordsRepo.isNeedFullSync(version);
    }
    public int getNewestVersion(){
        return sensitiveWordsRepo.getNewestVersion();
    }

    public APIResult retrieve(int pageNum, int pageSize, Map<String,List<String>> criteriaMap) {
        return sensitiveWordService.retrieve(pageNum, pageSize, criteriaMap);
    }

    public APIResult retrieveRepo(){
        return APIResult.buildResult(sensitiveWordsRepo.getVersionRepo());
    }
}
