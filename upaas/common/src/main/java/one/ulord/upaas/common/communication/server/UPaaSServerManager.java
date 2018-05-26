/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.server;

import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.BaseMessage;
import one.ulord.upaas.common.communication.UPaaSCommandCode;
import one.ulord.upaas.common.communication.UPaaSCommandHandler;
import one.ulord.upaas.common.communication.UPaaSCommandSession;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author haibo
 * @since 5/19/18
 */
@Slf4j
public class UPaaSServerManager {
    private static final String ALL_PATTERN = "*";

    private ConcurrentHashMap<String, UPaaSCommandSession> sessionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<UPaaSCommandHandler>> client2Handlers = new ConcurrentHashMap<>();
    private Set<UPaaSCommandHandler> clientAll2Handlers = new HashSet<>();
    private ConcurrentHashMap<String, Set<UPaaSCommandHandler>> command2Handlers = new ConcurrentHashMap<>();
    private Set<UPaaSCommandHandler> commandAll2Handlers = new HashSet<>();

    public void addSession(final UPaaSCommandSession session){
        log.debug("add session:{}", session);
        sessionMap.put(session.getPeerConnStr(), session);
        Set<UPaaSCommandHandler> handlers = getAllHandlers();
        if (handlers.size() > 0) {
            handlers.forEach(handler -> {
                handler.clientOnline(session);
            });
        }else{
            log.warn("No handler process session online:{}", session);
        }
    }

    public UPaaSCommandSession removeSession(String connstr){
        UPaaSCommandSession session = sessionMap.remove(connstr);
        log.debug("remove session:{}", session);
        if (session == null){
            log.warn("Session does not exist current.");
            return null;
        }

        Set<UPaaSCommandHandler> handlers = getAllHandlers();
        if (handlers.size() > 0) {
            handlers.forEach(handler -> {
                handler.clientOffline(session);
            });
        }else{
            log.warn("No handler process session online:{}", session);
        }
        return session;
    }

    public UPaaSCommandSession getSession(String connstr){
        return sessionMap.get(connstr);
    }

    /**
     * Register a message object handler
     * @param handler a command handler
     */
    public void registerHandler(UPaaSCommandHandler handler){
        registerHandler(ALL_PATTERN, ALL_PATTERN, handler);
    }

    /**
     * Register a message object handler
     * @param clientIds a client id list, or *
     * @param handler  a command handler
     */
    public void registerClientHandler(String clientIds, UPaaSCommandHandler handler){
        registerHandler(clientIds, null, handler);
    }
    /**
     * Register a message object handler
     * @param commands a command list or *
     * @param handler a command handler
     */
    public void registerCommandHandler(String commands, UPaaSCommandHandler handler){
        registerHandler(null, commands, handler);
    }


    /**
     * Register a message object handler
     * @param clientIds a client id list, or *
     * @param commands a command list or *
     * @param handler a command handler
     */
    public void registerHandler(String clientIds, String commands, UPaaSCommandHandler handler){
        if (handler == null){
            throw new NullPointerException("");
        }
        if (ALL_PATTERN.equals(clientIds)){
            clientAll2Handlers.add(handler);
        }else if (clientIds != null){
            addClientHandler(client2Handlers, clientIds, handler);
        }
        if (ALL_PATTERN.equals(commands)){
            commandAll2Handlers.add(handler);
        }else if (commands != null){
            addClientHandler(command2Handlers, commands, handler);
        }
    }


    /**
     * Process message from channel.
     * Server manager just dispatch current message to handler which has register to current manager.
     * @param message message object
     */
    public void processMessage(String connstr, final BaseMessage message) {
        final UPaaSCommandSession session = sessionMap.get(connstr);

        Set<UPaaSCommandHandler> handlers = getMessageHandlers(message);
        if (handlers.size() > 0) {
            handlers.forEach(handler -> {
                handler.commandReceived(session, message);
            });
        }else{
            log.warn("No handler process current message:{}", message);
        }
    }

    private Set<UPaaSCommandHandler> getMessageHandlers(final BaseMessage message){
        Set<UPaaSCommandHandler> handlers = new HashSet<>();
        Set<UPaaSCommandHandler> items;
        items = client2Handlers.get(message.getClientId());
        if (items != null && items.size() > 0){
            handlers.addAll(items);
        }
        if (clientAll2Handlers.size() > 0){
            handlers.addAll(clientAll2Handlers);
        }
        items = command2Handlers.get(String.valueOf(message.getCommand().getValue()));
        if (items != null && items.size() > 0){
            handlers.addAll(items);
        }
        // try to get command mode
        items = command2Handlers.get(String.valueOf(message.getCommand().getValue()
                & UPaaSCommandCode.COMMAND_MASK.getValue()));
        if (items != null && items.size() > 0){
            handlers.addAll(items);
        }
        if (commandAll2Handlers.size() > 0){
            handlers.addAll(items);
        }

        return handlers;
    }

    private Set<UPaaSCommandHandler> getAllHandlers(){
        Set<UPaaSCommandHandler> handlers = new HashSet<>();
        Set<UPaaSCommandHandler> items;
        items = client2Handlers.get(ALL_PATTERN);
        if (items != null && items.size() > 0){
            handlers.addAll(items);
        }
        items = command2Handlers.get(ALL_PATTERN);
        if (items != null && items.size() > 0){
            handlers.addAll(items);
        }

        return handlers;
    }

    private void addClientHandler(ConcurrentMap<String, Set<UPaaSCommandHandler>> map,
                                  String clientIds, UPaaSCommandHandler handler){
        String[] clients = clientIds.split(",");
        for (int i = 0; i < clients.length; i++){
            if (!StringUtils.isEmpty(clients[i])) {
                putHandler2Map(map, StringUtils.trim(clients[i]), handler);
            }
        }
    }


    private void putHandler2Map(ConcurrentMap<String, Set<UPaaSCommandHandler>> map,
                                String command, UPaaSCommandHandler handler){
        Set<UPaaSCommandHandler> handlers = map.get(command);
        if (handlers != null){
            handlers.add(handler);
        }else{
            handlers = new HashSet<>();
            handlers.add(handler);
            Set<UPaaSCommandHandler> preHandlers = map.putIfAbsent(command, handlers);
            if (preHandlers != null){
                preHandlers.add(handler); // we need update while put failed
            }
        }
    }

}
