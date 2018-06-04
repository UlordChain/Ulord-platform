/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.UPaaSErrorCode;
import one.ulord.upaas.common.communication.*;

import one.ulord.upaas.common.communication.bo.Response;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.utils.BeanUtilEx;
import one.ulord.upaas.uauth.common.vo.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Content auth sync command handler
 * @author haibo
 * @since 5/23/18
 */
@Slf4j
public class CAuthSyncCommandHandler extends UPaaSCommandHandlerAdapter {
    SensitiveWordsRepo sensitiveWordsRepo;
    CAuthSyncSubscriber cAuthSyncSubscriber;

    public CAuthSyncCommandHandler(SensitiveWordsRepo sensitiveWordsRepo, CAuthSyncSubscriber cAuthSyncSubscriber){
        this.sensitiveWordsRepo = sensitiveWordsRepo;
        this.cAuthSyncSubscriber = cAuthSyncSubscriber;
    }

    @Override
    public void clientOffline(UPaaSCommandSession session) {
        log.debug("Remove client subscriber:{}", session.getClientId());
        cAuthSyncSubscriber.removeClient(session.getClientId());
    }

    /**
     * Content auth sync command receive processor
     * @param session UPaaS command session
     * @param message a Message
     */
    @Override
    public void commandReceived(UPaaSCommandSession session, BaseMessage message) {
        BaseMessage resp = null;
        switch (message.getCommand()){
            case CAUTH_SYNC_VER_REQ:
                resp = processSyncVerReq(message);
                break;
            case CAUTH_SYNC_INCR_REQ:
                resp = processSyncIncrementReq(message);
                break;
            case CAUTH_SYNC_FULL_REQ:
                resp = processSyncFullReq(message);
                break;
            case CAUTH_SUBSCRIBE_REQ:
                resp = processSyncSubscribe(session, message);
                break;
        }

        if (resp != null){
            session.getCtx().writeAndFlush(resp);
        }else{
            log.warn("No response write to session:{} on message {}", session, message);
        }
    }

    private BaseMessage processSyncSubscribe(UPaaSCommandSession session, BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.CAUTH_SUBSCRIBE_REQ;

        Response response = new Response();
        if (!StringUtils.isEmpty(message.getClientId())){
            cAuthSyncSubscriber.addClient(message.getClientId(), session);
        }

        response.setSuccess(true);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SUBSCRIBE_RESP, response);
    }

    private BaseMessage processSyncFullReq(BaseMessage message) {
        UAuthSyncFullResponse fullResponse = new UAuthSyncFullResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof Map){
                UAuthSyncRequest syncRequest = new UAuthSyncRequest();
                try{
                    BeanUtilEx.populate(syncRequest, (Map<String, ? extends Object>) message.getObject());
                    List<SensitiveWord> items = sensitiveWordsRepo.getFullSensitiveWords();
                    fullResponse.setItems(items);
                    fullResponse.setVersion(sensitiveWordsRepo.getNewestVersion());
                    fullResponse.setSuccess(true);
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_FULL_RESP, fullResponse);
                } catch (IllegalAccessException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                } catch (InvocationTargetException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                }
            }
            fullResponse.setMessage("Invalid request object.");
            fullResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }else{
            fullResponse.setMessage("Invalid data type or no request object.");
            fullResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }

        fullResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_FULL_RESP, fullResponse);
    }

    private BaseMessage processSyncIncrementReq(BaseMessage message) {
        UAuthSyncIncrResponse incrResponse = new UAuthSyncIncrResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof Map){
                UAuthSyncRequest syncRequest = new UAuthSyncRequest();
                try {
                    BeanUtilEx.populate(syncRequest, (Map<String, ? extends Object>) message.getObject());
                    List<SyncOpItem<SensitiveWord>> syncOpItems = sensitiveWordsRepo.getIncrementSensitiveWords(syncRequest.getVersion());
                    incrResponse.setSyncOpItems(syncOpItems);
                    incrResponse.setVersion(sensitiveWordsRepo.getNewestVersion());
                    incrResponse.setSuccess(true);
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_INCR_RESP, incrResponse);
                } catch (IllegalAccessException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                } catch (InvocationTargetException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                }
            }
            incrResponse.setMessage("Invalid request object.");
            incrResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }else{
            incrResponse.setMessage("Invalid data type or no request object.");
            incrResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }

        incrResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_INCR_RESP, incrResponse);
    }

    private BaseMessage processSyncVerReq(BaseMessage message) {
        UAuthSyncVerResponse verResponse = new UAuthSyncVerResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof Map){
                UAuthSyncRequest syncRequest = new UAuthSyncRequest();
                try {
                    BeanUtilEx.populate(syncRequest, (Map<String, ? extends Object>) message.getObject());
                    boolean isNewestVer = sensitiveWordsRepo.isNewestVersion(syncRequest.getVersion());
                    verResponse.setFullSync(sensitiveWordsRepo.isNeedFullSync(syncRequest.getVersion()));
                    verResponse.setNewest(isNewestVer);
                    verResponse.setVersion(sensitiveWordsRepo.getNewestVersion());
                    verResponse.setSuccess(true);
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, verResponse);
                } catch (IllegalAccessException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                } catch (InvocationTargetException e) {
                    log.warn("parse UAuthSyncRequest object error:", e);
                }
            }
            verResponse.setMessage("Invalid request object.");
            verResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }else{
            verResponse.setMessage("Invalid data type or no request object.");
            verResponse.setErrorCode(UPaaSErrorCode.SERVER_INVALID_PARAMETER);
        }

        verResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, verResponse);
    }
}
