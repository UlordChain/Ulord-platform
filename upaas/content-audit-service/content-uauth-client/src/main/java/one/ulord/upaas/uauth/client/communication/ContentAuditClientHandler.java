/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.communication;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.bo.LoginRequest;
import one.ulord.upaas.common.communication.bo.Response;
import one.ulord.upaas.common.communication.client.UPaasClientHandler;
import one.ulord.upaas.common.utils.BeanUtilEx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author haibo
 * @since 5/24/18
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ContentAuditClientHandler extends UPaasClientHandler {
    @Value("${upaas.uauth.client.id}")
    private String clientId;
    @Value("${upaas.uauth.client.username}")
    private String username;
    @Value("${upaas.uauth.client.password}")
    private String password;



    private int seq = 1;


    @Override
    protected BaseMessage createLoginRequest() {
        LoginRequest loginRequest = new LoginRequest(username, password);
        BaseMessage message = new BaseMessage();
        message.setClientId(clientId);
        message.setType(BaseMessage.DATA_TYPE.POJO);
        message.setCommand(UPaaSCommandCode.LOGIN_REQ);
        message.setSeq((short)seq++);
        if (seq > Short.MAX_VALUE){
            seq = 1;
        }
        message.setObject(loginRequest);
        return message;
    }

    @Override
    protected void processMessage(ChannelHandlerContext channelHandlerContext, BaseMessage message) {
        switch (message.getCommand()){
            case LOGIN_REQ:
                log.warn("Invalid server response:{}", message);
                break;
            case LOGIN_RESP:
                processLoginResponse(message);
                break;
            case KEEPLIVE_REQ:
                log.warn("Invalid server response:{}", message);
                break;
            case KEEPLIVE_RESP:
                processKeepLiveRespnose(message);
                break;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
            // send keep live
            ctx.writeAndFlush(createKeepLiveRequest());
        }
        ctx.fireUserEventTriggered(evt);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // Private function

    private BaseMessage createKeepLiveRequest(){
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

    private void processLoginResponse(BaseMessage message) {
        assert message.getCommand() == UPaaSCommandCode.LOGIN_RESP;

        if (this.clientId.equals(message.getClientId())){
            if (message.getType() == BaseMessage.DATA_TYPE.POJO &&
                    message.getObject() instanceof Map){
                Response response = new Response();
                try {
                    BeanUtilEx.populate(response, (Map<String, ? extends Object>) message.getObject());
                    if (response.isSuccess()){
                        log.info("Keep live message has received.");
                    }else{
                        log.warn("Received a error login response:", response.getErrorCode(), response.getMessage());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }else {
                log.warn("Invalid login response message type:{}", message);
            }
        }else{
            log.warn("Received a invalid login message: clientId:{}", message.getClientId());
        }
    }

    private void processKeepLiveRespnose(BaseMessage message){
        assert message.getCommand() == UPaaSCommandCode.KEEPLIVE_RESP;

        if (this.clientId.equals(message.getClientId())){
            log.debug("A keep live response has received.");
        }else{
            log.warn("Received a invalid login message: clientId:{}", message.getClientId());
        }
    }
}
