/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;

import java.net.InetSocketAddress;

/**
 * @author haibo
 * @since 5/21/18
 */
@Slf4j
public abstract class UPaasClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String connStr = socketAddress.getHostName() + ":" + socketAddress.getPort();
        log.debug("Channel active:{}", connStr);

        BaseMessage message = createLoginRequest();
        ctx.writeAndFlush(message);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String connStr = socketAddress.getHostName() + ":" + socketAddress.getPort();

        log.debug("Channel Inactive:{}", connStr);
        connectDisconnted(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseMessage) {
            BaseMessage message = (BaseMessage)msg;
            // process message
            processMessage(ctx, message);
        }
    }

    /**
     * Send a login request to server after a client has crated
     * @return  message a message need to send to server
     */
    protected abstract BaseMessage createLoginRequest();

    /**
     * Process message after a message have received.
     * @param ctx
     * @param message
     */
    protected abstract void processMessage(ChannelHandlerContext ctx, BaseMessage message);

    protected void connectDisconnted(ChannelHandlerContext ctx){
        // do nothing
        // sub-class can override this method
    }
}
