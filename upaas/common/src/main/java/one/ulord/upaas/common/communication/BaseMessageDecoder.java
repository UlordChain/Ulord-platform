/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;

/**
 * +------------------------------------------------------------+
 * | LEN | SEQ | TYPE | CMD |            STREAM                 |
 * +------------------------------------------------------------+
 * @author haibo
 * @since 5/17/18
 */
@Slf4j
public class BaseMessageDecoder extends ByteToMessageDecoder {
    private static final int HEAD_LEN = 12;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        BaseMessage baseMessage = objectMapper.readValue(data, BaseMessage.class);
        out.add(baseMessage);
    }
}
