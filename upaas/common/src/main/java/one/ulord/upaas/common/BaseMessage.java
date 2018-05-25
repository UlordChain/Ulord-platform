/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common;

import lombok.Data;
import one.ulord.upaas.common.communication.UPaaSCommandCode;


/**
 * @author haibo
 * @since 5/17/18
 */
@Data
public class BaseMessage {
    private short seq;
    private DATA_TYPE type;
    private String clientId;
    private UPaaSCommandCode command;
    private byte[] stream;
    private Object object;

    public enum DATA_TYPE{
        BINARY(0),
        POJO(1);

        int value;
        DATA_TYPE(int value){
            this.value = value;
        }

        public static DATA_TYPE of(int value){
            switch(value){
                case 0: return BINARY;
                case 1: return POJO;
            }
            throw new RuntimeException("Unsupported value:" + value);
        }

        public int getValue(){
            return value;
        }
    }
}
