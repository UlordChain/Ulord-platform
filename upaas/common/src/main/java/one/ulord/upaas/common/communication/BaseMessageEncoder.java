/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;

/**
 * @author haibo
 * @since 5/17/18
 */
@Slf4j
public class BaseMessageEncoder extends MessageToMessageEncoder<BaseMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage msg, List<Object> out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] result = objectMapper.writeValueAsBytes(msg);
        out.add(Unpooled.wrappedBuffer(result));
    }
}
