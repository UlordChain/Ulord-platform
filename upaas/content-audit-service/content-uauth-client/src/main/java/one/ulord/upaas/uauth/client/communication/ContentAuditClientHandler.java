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
import one.ulord.upaas.common.communication.client.UPaasClientHandler;
import one.ulord.upaas.uauth.client.contentauth.business.SensitiveWordSyncer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haibo
 * @since 5/24/18
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ContentAuditClientHandler extends UPaasClientHandler {
    @Autowired
    SensitiveWordSyncer sensitiveWordSyncer;

    private int seq = 1;


    @Override
    protected BaseMessage createLoginRequest() {
        return sensitiveWordSyncer.requestLogin();
    }

    @Override
    protected void processMessage(ChannelHandlerContext ctx, BaseMessage message) {
        BaseMessage req = null;
        switch (message.getCommand()){
            case LOGIN_REQ:
                log.warn("Invalid server response:{}", message);
                break;
            case LOGIN_RESP:
                req = sensitiveWordSyncer.processLoginResponse(message);
                break;
            case KEEPLIVE_REQ:
                log.warn("Invalid server response:{}", message);
                break;
            case KEEPLIVE_RESP:
                req = sensitiveWordSyncer.processKeepLiveResponse(message);
                break;
            case CAUTH_SYNC_VER_RESP:
                req = sensitiveWordSyncer.processVersionResponse(message);
                break;
            case CAUTH_SYNC_INCR_RESP:
                req = sensitiveWordSyncer.processIncrementSyncResponse(message);
                break;
            case CAUTH_SYNC_FULL_RESP:
                req = sensitiveWordSyncer.processFullSyncResponse(message);
                break;
            case CAUTH_SUBSCRIBE_RESP:
                log.info("Subscribe success.");
                break;
            case CAUTH_SYNC_VER_CHG_NOTIFY:
                req = sensitiveWordSyncer.processVersionChangeNotify(message);
                break;

        }
        if (req != null) {
            ctx.writeAndFlush(req);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
            // send keep live
            ctx.writeAndFlush(sensitiveWordSyncer.createKeepLiveRequest());
        }
        ctx.fireUserEventTriggered(evt);
    }

}
