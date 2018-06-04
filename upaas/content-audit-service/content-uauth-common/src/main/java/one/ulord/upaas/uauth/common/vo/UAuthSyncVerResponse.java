/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import one.ulord.upaas.common.communication.bo.Response;

/**
 * @author haibo
 * @since 5/24/18
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class UAuthSyncVerResponse extends Response {
    boolean isNewest;
    boolean isFullSync;
    int version;
}

