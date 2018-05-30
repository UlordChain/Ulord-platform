/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

/**
 * @author haibo
 * @since 5/22/18
 */
public enum UPaaSCommandCode {
    COMMAND_MASK          ( ~0x00FF),

    BASE_TYPE                        ( 0x0000),
    KEEPLIVE_REQ                     ( 0x0001),
    KEEPLIVE_RESP                    ( 0x0002),
    LOGIN_REQ                        ( 0x0003),
    LOGIN_RESP                       ( 0x0004),

    CAUTH_SYNC_TYPE                  ( 0x0100),
    CAUTH_SYNC_VER_REQ               ( 0x0101),
    CAUTH_SYNC_VER_RESP              ( 0x0102),
    CAUTH_SYNC_INCR_REQ              ( 0x0103),
    CAUTH_SYNC_INCR_RESP             ( 0x0104),
    CAUTH_SYNC_FULL_REQ              ( 0x0105),
    CAUTH_SYNC_FULL_RESP             ( 0x0106),
    CAUTH_SUBSCRIBE_REQ              ( 0x0107),
    CAUTH_SUBSCRIBE_RESP             ( 0x0108),
    CAUTH_SYNC_VER_CHG_NOTIFY        ( 0x0109); // Notify message no response be need

    int value;
    UPaaSCommandCode(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
