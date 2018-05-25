/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.UPaaSCommandSession;

import java.net.InetSocketAddress;

/**
 * UPaaS Server Communication Handler
 * @author haibo
 * @since 5/19/18
 */
@Slf4j
public class UPaaSServerHandler extends ChannelInboundHandlerAdapter {
    public UPaaSServerManager serverManager;

    public UPaaSServerHandler(UPaaSServerManager serverManager){
        this.serverManager = serverManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String connStr = socketAddress.getHostName() + ":" + socketAddress.getPort();

        log.debug("A client has connected:{}", connStr);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String connStr = socketAddress.getHostName() + ":" + socketAddress.getPort();

        log.debug("A client has disconnected:{}", connStr);
        serverManager.removeSession(connStr); // remove session from server manager
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String connStr = socketAddress.getHostName() + ":" + socketAddress.getPort();
        if (msg instanceof BaseMessage) {
            BaseMessage message = (BaseMessage)msg;
            log.debug("Rx: Id:{} Conn:{}, Type:{}, Cmd:{}", message.getClientId(),
                    connStr, message.getType(), message.getCommand());
            if (serverManager.getSession(connStr) == null) {
                serverManager.addSession(new UPaaSCommandSession(connStr, message.getClientId(), ctx));
            }

            // process message
            serverManager.processMessage(connStr, message);
        }
    }


}
