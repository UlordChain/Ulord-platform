/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.communication;

import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.UPaaSErrorCode;
import one.ulord.upaas.common.communication.*;
import one.ulord.upaas.common.communication.bo.LoginRequest;
import one.ulord.upaas.common.communication.bo.LoginResponse;
import one.ulord.upaas.common.communication.server.UPaaSServerManager;
import one.ulord.upaas.common.utils.BeanUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author haibo
 * @since 5/22/18
 */
@Slf4j
@Component
public class CAuthServerCommandHandler extends UPaaSCommandHandlerAdapter{
    @Autowired
    UPaaSServerManager uPaaSServerManager;

    private boolean needCloseClient = false;

    public void commandReceived(UPaaSCommandSession session, BaseMessage message) {
        BaseMessage resp = null;
        needCloseClient = false;
        switch (message.getCommand()){
            case LOGIN_REQ:
                resp = processLoginReq(message);
                break;
            case KEEPLIVE_REQ:
                resp = UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.KEEPLIVE_RESP);
                break;
        }

        if (resp != null){
            session.getCtx().writeAndFlush(resp);
            if (needCloseClient){
                session.getCtx().close();
            }
        }else{
            log.warn("No response write to session:{} on message {}", session, message);
        }
    }

    /**
     * Process login request and return a response.
     * We need to check authorization
     * @param message
     * @return
     */
    private BaseMessage processLoginReq(BaseMessage message) {
        LoginResponse loginResponse = new LoginResponse();
        if (message.getType() == BaseMessage.DATA_TYPE.POJO
                && message.getObject() != null){
            if (message.getObject() instanceof Map){
                LoginRequest loginRequest = new LoginRequest();
                try {
                    BeanUtilEx.populate(loginRequest, (Map<String, ? extends Object>) message.getObject());
                    if (loginRequest != null){
                        // TODO: auth user

                        loginResponse.setSuccess(true);
                        loginResponse.setMessage("Login success");

                        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.LOGIN_RESP, loginResponse);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            loginResponse.setMessage("Invalid request object.");
            loginResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }else{
            loginResponse.setMessage("Invalid data type or no request object.");
            loginResponse.setErrorCode(UPaaSErrorCode.SRVERR_INVALID_PARAMETER);
        }

        loginResponse.setSuccess(false);
        needCloseClient = true; // close client connection

        return UPaaSCommandUtils.respMessage(message, UPaaSCommandCode.LOGIN_RESP, loginResponse);
    }


}
