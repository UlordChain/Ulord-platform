/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import one.ulord.upaas.common.communication.BaseMessageDecoder;
import one.ulord.upaas.common.communication.BaseMessageEncoder;

/**
 * @author haibo
 * @since 5/19/18
 */
public class UPaaSCommandServer  {
    private int port;
    private UPaaSServerManager serverManager;

    public UPaaSCommandServer(int port){
        this.serverManager = new UPaaSServerManager();
        this.port = port;
    }

    public UPaaSServerManager getServerManager(){
        return serverManager;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new BaseMessageDecoder())
                                    .addLast(new BaseMessageEncoder())
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new UPaaSServerHandler(serverManager));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
