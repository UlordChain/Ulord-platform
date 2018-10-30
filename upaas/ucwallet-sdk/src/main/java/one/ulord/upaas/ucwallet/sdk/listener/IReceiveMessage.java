/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.listener;


import one.ulord.upaas.ucwallet.sdk.remote.MQMessage;

/**
 * Message process interface
 *
 * @author yinhaibo
 * @since 2018
 */
public interface IReceiveMessage {
    void handleResponse(MQMessage transactionResponse);
}
