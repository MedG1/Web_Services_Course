package tn.example.samplews.boundaries;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import tn.example.samplews.controllers.MessageEventManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@ServerEndpoint(value = "/pushes", encoders = {JSONTextEncoder.class}, decoders = {JSONTextDecoder.class})
public class PushWebSocketEndpoint {
    @Inject
    private Logger log;
    @EJB
    private MessageEventManager messageEventManager;


    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    public static void broadcastMessage(JsonObject message){
        for(Session session: sessions){
            try {
                session.getBasicRemote().sendObject(message);
            } catch(IOException | EncodeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session){
        log.info("Push WebSocket opened with ID: " + session.getId());
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        log.info("Push WebSocket closed for ID:" + session.getId() + " with reason: [" + reason.getCloseCode() + ": " + reason.getReasonPhrase() + "]");
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error){
        log.warning("Push WebSocket error for ID " + session.getId() + ": " + error.getMessage());
    }

    @OnMessage
    public void onMessage(JsonObject message, Session session){
        if(session.isOpen() && session.isSecure()){
            messageEventManager.publishFromClient(message);
        }
    }




}
