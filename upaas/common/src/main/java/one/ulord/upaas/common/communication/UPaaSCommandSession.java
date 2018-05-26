/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import one.ulord.upaas.common.BaseMessage;


/**
 * @author haibo
 * @since 5/19/18
 */
@Data
public class UPaaSCommandSession {
    /**
     * Content auth client connection information.
     */
    private String peerConnStr;
    /**
     * Content auth client ID
     */
    private String clientId;
    /**
     * A netty channel handler context
     */
    private ChannelHandlerContext ctx;

    public UPaaSCommandSession(String connstr, String clientId, ChannelHandlerContext ctx){
        this.peerConnStr = connstr;
        this.clientId = clientId;
        this.ctx = ctx;
    }

    public void writeMessage(BaseMessage message){
        this.ctx.writeAndFlush(message);
    }
}
