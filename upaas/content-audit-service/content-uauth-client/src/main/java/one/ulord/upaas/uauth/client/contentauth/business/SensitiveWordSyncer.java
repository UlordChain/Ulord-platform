/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.contentauth.business;

import com.hankcs.hanlp.dictionary.CustomDictionary;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.bo.LoginRequest;
import one.ulord.upaas.common.communication.bo.Response;
import one.ulord.upaas.common.sync.SyncOpEnum;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.sync.SyncOpVersionRepo;
import one.ulord.upaas.common.utils.BeanUtilEx;
import one.ulord.upaas.uauth.common.vo.*;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sensitive word synchronization
 * @author haibo
 * @since 5/30/18
 */
@Component
@Slf4j
public class SensitiveWordSyncer {
    @Value("${upaas.uauth.client.id}")
    private String clientId;
    @Value("${upaas.uauth.client.username}")
    private String username;
    @Value("${upaas.uauth.client.password}")
    private String password;

    private int seq = 1;

    /**
     * Sync operation version repo for sensitive word
     */
    SyncOpVersionRepo<SensitiveWord> sensitiveWordsRepo = new SyncOpVersionRepo<>();
    HashMap<String, SensitiveWord> sensitiveWordMap = new HashMap<>();

    /////////////////////////////////////////////////////////////////////////////////////////
    // Basic
    private <T extends Response> T getMapFromMessage(BaseMessage message, Class<T> classz){
        if (this.clientId.equals(message.getClientId())){
            if (message.getType() == BaseMessage.DATA_TYPE.POJO &&
                    message.getObject() instanceof Map){
                T response = createInstance(classz);
                Mapper mapper = DozerBeanMapperBuilder.buildDefault();
                mapper.map(message.getObject(), response);
                if (response.isSuccess()){
                    return response;
                }else{
                    log.warn("Received a error login response:", response.getErrorCode(), response.getMessage());
                }
            }else {
                log.warn("Invalid login response message type:{}", message);
            }
        }else{
            log.warn("Received a invalid login message: clientId:{}", message.getClientId());
        }

        return null;
    }

    public static <T> T createInstance(Class<T> cls) {
        T obj;
        try {
            obj=cls.newInstance();
        } catch (Exception e) {
            obj=null;
        }
        return obj;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // Login
    public BaseMessage requestLogin() {
        LoginRequest loginRequest = new LoginRequest(username, password);
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.LOGIN_REQ);
        message.setSeq((short) seq++);
        if (seq > Short.MAX_VALUE) {
            seq = 1;
        }
        message.setObject(loginRequest);

        return message;
    }

    public BaseMessage processLoginResponse(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.LOGIN_RESP;
        Response response = getMapFromMessage(message, Response.class);
        if (response != null){
            return requestVersion();
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // Version

    public BaseMessage requestVersion() {
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.CAUTH_SYNC_VER_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }

        // add current version data into message
        UAuthSyncRequest request = new UAuthSyncRequest();
        request.setVersion(sensitiveWordsRepo.getNewestVersion());
        message.setObject(request);
        return message;
    }

    public BaseMessage processVersionResponse(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.CAUTH_SYNC_VER_RESP;

        UAuthSyncVerResponse response = getMapFromMessage(message, UAuthSyncVerResponse.class);
        if (response != null){
            if (response.isNewest()){
                // subscribe new message
                return subscribeSync();
            }else if(response.isFullSync()) {
                // request full sync
                return requestFullSyncRequest();
            }else{
                // increment sync
                return requestIncrementSyncRequest();
            }
        }else {
            log.warn("Error version response.");
            return null;
        }
    }

    private BaseMessage requestIncrementSyncRequest() {
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.CAUTH_SYNC_INCR_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }

        // add current version data into message
        UAuthSyncRequest request = new UAuthSyncRequest();
        request.setVersion(sensitiveWordsRepo.getNewestVersion());
        message.setObject(request);
        return message;
    }

    private BaseMessage requestFullSyncRequest() {
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.CAUTH_SYNC_FULL_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }

        // add current version data into message
        UAuthSyncRequest request = new UAuthSyncRequest();
        request.setVersion(sensitiveWordsRepo.getNewestVersion());
        message.setObject(request);
        return message;
    }

    private BaseMessage subscribeSync() {
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.CAUTH_SUBSCRIBE_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }

        return message;
    }

    public BaseMessage createKeepLiveRequest(){
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.KEEPLIVE_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }
        return message;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // Keep live
    public BaseMessage processKeepLiveResponse(BaseMessage message){
        assert message.getCommand() == UPaaSCommandCode.KEEPLIVE_RESP;

        if (this.clientId.equals(message.getClientId())){
            log.debug("A keep live response has received.");
        }else{
            log.warn("Received a invalid login message: clientId:{}", message.getClientId());
        }

        return null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // Sync
    public BaseMessage processIncrementSyncResponse(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.CAUTH_SYNC_INCR_RESP;

        UAuthSyncIncrResponse response = getMapFromMessage(message, UAuthSyncIncrResponse.class);
        if (response != null){
            if (response.getVersion() > sensitiveWordsRepo.getNewestVersion()){
                // update current repo
                sensitiveWordsRepo.addRecordOp(response.getSyncOpItems());
                updateNLPDictionary(response.getSyncOpItems());
                // update current version
                sensitiveWordsRepo.updateVersion(response.getVersion());
            }
        }else {
            log.warn("Error increment sync response.");
        }
        return subscribeSync();
    }

    public BaseMessage processFullSyncResponse(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.CAUTH_SYNC_FULL_RESP;
        UAuthSyncFullResponse response = getMapFromMessage(message, UAuthSyncFullResponse.class);
        if (response != null){
            if (response.getVersion() >= sensitiveWordsRepo.getNewestVersion()){
                // update current repo
                List<SyncOpItem<SensitiveWord>> syncOpItems = sensitiveWordsRepo.wrapSyncOpItem(
                        SyncOpEnum.ADD, response.getItems());
                sensitiveWordsRepo.addRecordOp(syncOpItems);
                updateNLPDictionary(syncOpItems);
            }
        }else {
            log.warn("Error full sync response.");
        }
        return subscribeSync();
    }

    public BaseMessage processVersionChangeNotify(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.CAUTH_SYNC_VER_CHG_NOTIFY;

        if (this.clientId.equals(message.getClientId())){
            if (message.getType() == BaseMessage.DATA_TYPE.POJO &&
                    message.getObject() instanceof Map){
                UAuthSyncRequest request = new UAuthSyncRequest();
                Mapper mapper = DozerBeanMapperBuilder.buildDefault();
                mapper.map(message.getObject(), request);
                if (request.getVersion() != sensitiveWordsRepo.getNewestVersion()){
                    return requestVersion();
                }
            }else {
                log.warn("Invalid login response message type:{}", message);
            }
        }else{
            log.warn("Received a invalid login message: clientId:{}", message.getClientId());
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // NLP

    /**
     * Hit sensitive word
     * @param word word
     * @return sensitive word
     */
    public SensitiveWord hitSensitiveWord(String word){
        return sensitiveWordMap.get(word);
    }

    private void updateNLPDictionary(List<SyncOpItem<SensitiveWord>> syncOpItems) {
        syncOpItems.forEach(sensitiveWordSyncOpItem -> {
            switch (sensitiveWordSyncOpItem.getOp()){
                case ADD:
                    String keyword = sensitiveWordSyncOpItem.getData().getKeyword();
                    CustomDictionary.add(keyword);
                    sensitiveWordMap.put(keyword, sensitiveWordSyncOpItem.getData());
                    log.trace("Add NLP keyword", keyword);
                    break;
                case DELETE:
                    keyword = sensitiveWordSyncOpItem.getData().getKeyword();
                    CustomDictionary.remove(keyword);
                    sensitiveWordMap.remove(keyword);
                    log.trace("Delete NLP keyword", keyword);
                    break;
            }
        });
    }

}
