/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.business;

import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.UPaaSCommandSession;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.*;

/**
 * Content auth sync subscriber, any client can send a subscribe request to add here, then
 * while version has changed, a message can send by subscriber
 * @author haibo
 * @since 5/30/18
 */
public class CAuthSyncSubscriber {
    private ConcurrentHashMap<String, UPaaSCommandSession> mapSubscribeAuthSync;
    private ScheduledExecutorService scheduledThreadPool;

    @Value("${upaas.cauth.server.subscriber.thread-pool-size:5}")
    int threadPoolSize;

    public CAuthSyncSubscriber(){
        mapSubscribeAuthSync = new ConcurrentHashMap<>();
        scheduledThreadPool = Executors.newScheduledThreadPool(threadPoolSize);
    }

    /**
     * Add a subscriber to current manager
     * @param clientId client id
     * @param session  client session
     * @return old session object or null
     */
    public UPaaSCommandSession addClient(String clientId, UPaaSCommandSession session){
        UPaaSCommandSession oldSession = mapSubscribeAuthSync.putIfAbsent(clientId, session);
        if (oldSession != null){
            mapSubscribeAuthSync.put(clientId, session); // replace
        }

        return oldSession;
    }

    /**
     * Remove client from current manager
     * @param clientId
     */
    public void removeClient(String clientId){
        mapSubscribeAuthSync.remove(clientId);
    }

    /**
     * Broadcast message
     * @param message a message which need send
     */
    public void broadcastMessage(final BaseMessage message){
        scheduledThreadPool.schedule(()->
                mapSubscribeAuthSync.forEachValue(5, session ->{
                    message.setClientId(session.getClientId());
                    session.writeMessage(message);
                }), 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Send message to specified client
     * @param clientId client id
     * @param message message
     * @return
     */
    public boolean sendMessage(String clientId, final BaseMessage message){
        UPaaSCommandSession session = mapSubscribeAuthSync.get(clientId);
        if (session != null){
            scheduledThreadPool.schedule(()->
                session.writeMessage(message), 0, TimeUnit.MILLISECONDS);
            return true;
        }else{
            return false;
        }
    }
}
