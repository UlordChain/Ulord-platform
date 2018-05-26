/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication;

import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;

/**
 * @author haibo
 * @since 5/22/18
 */
@Slf4j
public class UPaaSCommandHandlerAdapter implements UPaaSCommandHandler {
    @Override
    public void clientOnline(UPaaSCommandSession session) {
        log.debug("A client is online:{}", session);
    }

    @Override
    public void clientOffline(UPaaSCommandSession session) {
        log.debug("A client is offline:{}", session);
    }

    @Override
    public void commandReceived(UPaaSCommandSession session, BaseMessage message) {
        log.debug("Receive a command {} from client:{}", message, session);
    }

    @Override
    public int sendCommand(UPaaSCommandSession session, BaseMessage message) {
        log.debug("Send a command {} to client:{}", message, session);
        return 0;
    }
}
