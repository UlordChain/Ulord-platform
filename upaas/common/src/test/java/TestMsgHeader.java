/**
 * Copyright(c) 2018
 * Ulord core developers
 */

import one.ulord.upaas.common.ContentAuthMsgHeader;
import one.ulord.upaas.common.MsgPackTools;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

/**
 * @author haibo
 * @since 5/17/18
 */
public class TestMsgHeader {
    public void testMsgHeader(){
        ContentAuthMsgHeader contentAuthMsgHeader = new ContentAuthMsgHeader.Builder()
                .clientId("1234567").build();
    }


    @Test
    public void testMsgPackProtocol(){
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        try {
            packer.packLong(19);
            packer.close(); // Never forget to close (or flush) the buffer
            byte[] data = packer.toByteArray();
            System.out.println(Hex.encodeHexString(data));

            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data);
            System.out.println(unpacker.unpackLong());
            System.out.println(MsgPackTools.getIntFormatLen(data[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            packer = MessagePack.newDefaultBufferPacker();
            packer.packLong(1800000000000000000L);
            packer.close(); // Never forget to close (or flush) the buffer
            byte[] data = packer.toByteArray();
            System.out.println(Hex.encodeHexString(data));
            System.out.println(MsgPackTools.getIntFormatLen(data[0]));

            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data);
            System.out.println(unpacker.unpackLong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
