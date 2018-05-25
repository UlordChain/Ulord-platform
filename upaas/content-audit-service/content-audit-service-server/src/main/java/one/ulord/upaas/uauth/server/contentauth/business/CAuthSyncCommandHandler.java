/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.*;

import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.uauth.common.vo.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author haibo
 * @since 5/23/18
 */
@Slf4j
public class CAuthSyncCommandHandler extends UPaaSCommandHandlerAdapter {

    @Autowired
    SensitiveWordsRepo sensitiveWordsRepo;

    public void commandReceived(UPaaSCommandSession session, BaseMessage message) {
        BaseMessage resp = null;
        switch (message.getCommand()){
            case CAUTH_SYNC_VER_REQ:
                resp = processSyncVerReq(message);
                break;
            case CAUTH_SYNC_INCR_REQ:
                resp = processSyncIncrReq(message);
                break;
            case CAUTH_SYNC_FULL_REQ:
                resp = processSyncFullReq(message);
                break;
        }

        if (resp != null){
            session.getCtx().writeAndFlush(resp);
        }else{
            log.warn("No response write to session:{} on message {}", session, message);
        }
    }

    private BaseMessage processSyncFullReq(BaseMessage message) {
        UAuthSyncFullResponse fullResponse = new UAuthSyncFullResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof UAuthSyncRequest){
                UAuthSyncRequest syncRequest = (UAuthSyncRequest)message.getObject();
                if (syncRequest != null){
                    List<SensitiveWords> items = sensitiveWordsRepo.getFullSensitiveWords();
                    fullResponse.setItems(items);
                    fullResponse.setVersion(syncRequest.getVersion());
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, fullResponse);
                }
            }
            fullResponse.setMessage("Invalid request object.");
            fullResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }else{
            fullResponse.setMessage("Invalid data type or no request object.");
            fullResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }

        fullResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, fullResponse);
    }

    private BaseMessage processSyncIncrReq(BaseMessage message) {
        UAuthSyncIncrResponse incrResponse = new UAuthSyncIncrResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof UAuthSyncRequest){
                UAuthSyncRequest syncRequest = (UAuthSyncRequest)message.getObject();
                if (syncRequest != null){
                    List<SyncOpItem<SensitiveWords>> syncOpItems = sensitiveWordsRepo.getIncrementSensitiveWords(syncRequest.getVersion());
                    incrResponse.setSyncOpItems(syncOpItems);
                    incrResponse.setVersion(syncRequest.getVersion());
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, incrResponse);
                }
            }
            incrResponse.setMessage("Invalid request object.");
            incrResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }else{
            incrResponse.setMessage("Invalid data type or no request object.");
            incrResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }

        incrResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, incrResponse);
    }

    private BaseMessage processSyncVerReq(BaseMessage message) {
        UAuthSyncVerResponse verResponse = new UAuthSyncVerResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof UAuthSyncRequest){
                UAuthSyncRequest syncRequest = (UAuthSyncRequest)message.getObject();
                if (syncRequest != null){
                    boolean isNewestVer = sensitiveWordsRepo.isNewestVersion(syncRequest.getVersion());
                    verResponse.setNewest(isNewestVer);
                    verResponse.setVersion(syncRequest.getVersion());
                    return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, verResponse);
                }
            }
            verResponse.setMessage("Invalid request object.");
            verResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }else{
            verResponse.setMessage("Invalid data type or no request object.");
            verResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }

        verResponse.setSuccess(false);

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.CAUTH_SYNC_VER_RESP, verResponse);
    }
}
