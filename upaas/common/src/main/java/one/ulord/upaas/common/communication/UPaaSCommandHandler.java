/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import one.ulord.upaas.common.BaseMessage;

/**
 * UPaaS Command Handler
 * @author haibo
 * @since 5/19/18
 */
public interface UPaaSCommandHandler {
    void clientOnline(UPaaSCommandSession session);
    void clientOffline(UPaaSCommandSession session);
    void commandReceived(UPaaSCommandSession session, BaseMessage message);
    int sendCommand(UPaaSCommandSession session, BaseMessage message);
}
