/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import one.ulord.upaas.common.communication.bo.Response;
import one.ulord.upaas.common.sync.SyncOpItem;

import java.util.List;

/**
 * @author haibo
 * @since 5/24/18
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class UAuthSyncIncrResponse extends Response {
    int version;
    List<SyncOpItem<SensitiveWord>> syncOpItems;
}