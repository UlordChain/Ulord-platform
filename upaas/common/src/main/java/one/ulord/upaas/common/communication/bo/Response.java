/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.bo;

import lombok.Data;

/**
 * @author haibo
 * @since 5/23/18
 */
@Data
public class Response {
    boolean success;
    int errorCode;
    String message;
}
