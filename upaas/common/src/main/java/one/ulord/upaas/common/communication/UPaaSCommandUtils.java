/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import one.ulord.upaas.common.BaseMessage;

/**
 * @author haibo
 * @since 5/24/18
 */
public class UPaaSCommandUtils {
    public static BaseMessage respMessage(BaseMessage message, UPaaSCommandCode respCommand, Object object){
        BaseMessage resp = new BaseMessage();
        resp.setSeq(message.getSeq());
        resp.setType(BaseMessage.DATA_TYPE.POJO); // POJO type
        resp.setCommand(respCommand);
        resp.setClientId(message.getClientId());
        resp.setObject(object);

        return resp;
    }

    public static BaseMessage respMessage(BaseMessage message, UPaaSCommandCode respCommand){
        return respMessage(message, respCommand, null);
    }
}
