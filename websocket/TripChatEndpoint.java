package websocket;

import dao.ChatMessageDAO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/trip-chat/{tripId}")
public class TripChatEndpoint {

    private static final ConcurrentHashMap<String, Set<Session>> tripChats = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("tripId") String tripId) {
        tripChats.putIfAbsent(tripId, ConcurrentHashMap.newKeySet());
        tripChats.get(tripId).add(session);
    }

    @OnMessage
    public void onMessage(String fullMessage, Session session, @PathParam("tripId") String tripId) {
        if (fullMessage == null || fullMessage.trim().isEmpty()) return;
        if (fullMessage.length() > 500) return;

        String username = "unknown";
        String message = fullMessage;

        if (fullMessage.contains(": ")) {
            String[] parts = fullMessage.split(": ", 2);
            username = parts[0];
            message = parts[1];
        }

        ChatMessageDAO dao = new ChatMessageDAO();
        dao.saveMessage(Integer.parseInt(tripId), username, message);

        Set<Session> sessions = tripChats.get(tripId);

        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(fullMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("tripId") String tripId) {
        Set<Session> sessions = tripChats.get(tripId);
        if (sessions != null) sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}