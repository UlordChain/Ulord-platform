/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Tool For MessagePack
 * @author haibo
 * @since 5/17/18
 */
public class MsgPackTools {
    public static int getIntFormatLen(byte head){
        byte b = head;
        if (MessagePack.Code.isFixInt(b)) {
            return 1;
        }
        switch (b) {
            case MessagePack.Code.UINT8: // unsigned int 8
                return 2;
            case MessagePack.Code.UINT16: // unsigned int 16
                return 3;
            case MessagePack.Code.UINT32: // unsigned int 32
                return 5;
            case MessagePack.Code.UINT64: // unsigned int 64
                return 9;
            case MessagePack.Code.INT8: // signed int 8
                return 2;
            case MessagePack.Code.INT16: // signed int 16
                return 3;
            case MessagePack.Code.INT32: // signed int 32
                return 5;
            case MessagePack.Code.INT64: // signed int 64
                return 9;
        }
        throw new RuntimeException("Unexpected MessagePack INT Format Head.");
    }

    public static Map<String, Object> unpackMap(MessageUnpacker unpacker) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();

        int n = unpacker.unpackMapHeader();
        boolean keyRead = false;
        String key;
        Object value;
        for (int i = 0; i < n; i++){
            // get key
            Value v = unpacker.unpackValue();
            if (v.getValueType() != ValueType.STRING){
                throw new RuntimeException("Unexpected map value type.");
            }
            key = v.asStringValue().asString();
            value = null;

            // get value
            switch (v.getValueType()) {
                case NIL:
                    v.isNilValue(); // true
                    value = null;
                    break;
                case BOOLEAN:
                    boolean b = v.asBooleanValue().getBoolean();
                    value = b;
                    break;
                case INTEGER:
                    IntegerValue iv = v.asIntegerValue();
                    if (iv.isInIntRange()) {
                        value = iv.toInt();
                    }
                    else if (iv.isInLongRange()) {
                        value = iv.toLong();
                    }
                    else {
                        value = iv.toBigInteger();
                    }
                    break;
                case FLOAT:
                    FloatValue fv = v.asFloatValue();
                    value = fv.toDouble(); // use as double
                    break;
                case STRING:
                    value = v.asStringValue().asString();
                    break;
                case BINARY:
                    value = v.asBinaryValue().asByteArray();
                    break;
                case ARRAY:
                    ArrayValue a = v.asArrayValue();
                    for (Value e : a) {
                        System.out.println("read array element: " + e);
                    }
                    break;
                case EXTENSION:
                    ExtensionValue ev = v.asExtensionValue();
                    value = ev.getData();
                    break;
                default:
                    throw new RuntimeException("Unexpected data type:" + v.getValueType().toString());
            }

            resultMap.put(key, value);
        }

        return resultMap;
    }
}
