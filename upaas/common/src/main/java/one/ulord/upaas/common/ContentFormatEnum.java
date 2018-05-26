/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common;

/**
 * Ulord PaaS Content Format Enum
 * @author haibo
 * @since 5/16/18
 */
public enum ContentFormatEnum {
    TEXT(0),
    HTML(1),
    PDF(2),
    PICTURE(3);

    private int value;
    private ContentFormatEnum(int value){
        this.value = value;
    }
}
