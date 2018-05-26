/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.communication.BaseMessageDecoder;
import one.ulord.upaas.common.communication.BaseMessageEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @author haibo
 * @since 5/19/18
 */
@Slf4j
public class UPaaSCommandClient  {
    private String host;
    private int port;
    private UPaasClientHandler handler;
    private long idleInterval = 30;

    public UPaaSCommandClient(String host, int port, UPaasClientHandler handler){
        this.host = host;
        this.port = port;
        this.handler = handler;
    }

    public UPaaSCommandClient(String host, int port, UPaasClientHandler handler, long idleInterval){
        this.host = host;
        this.port = port;
        this.handler = handler;
        this.idleInterval = idleInterval;
    }

    private Bootstrap createNewBootstrap(EventLoopGroup workerGroup){
        Bootstrap b = new Bootstrap(); // (1)
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

                ch.pipeline()
                        .addLast(new IdleStateHandler(idleInterval, idleInterval,idleInterval, TimeUnit.SECONDS))
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(new LengthFieldPrepender(4))
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new BaseMessageDecoder())
                        .addLast(new BaseMessageEncoder())
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(handler);
            }
        });

        return b;
    }
    public void run(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            double count = 1;
            while(true) {
                try {
                    Bootstrap b = createNewBootstrap(workerGroup);
                    // Start the client.
                    ChannelFuture f = b.connect(host, port).sync(); // (5)
                    f.sync();
                    if (f.isSuccess()) {
                        count = 1; // success connect to server, reset count to init value
                    }
                    // Wait until the connection is closed.
                    f.channel().closeFuture().sync();
                }catch (Exception e){
                    log.warn("Connect exception:{}", e.getMessage());
                }

                log.info("Client disconnect from server, try to reconnect...");
                Thread.sleep((long)(10000 * Math.log10(count)));
                count ++;
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
