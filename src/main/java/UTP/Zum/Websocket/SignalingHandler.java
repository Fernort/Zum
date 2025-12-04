package UTP.Zum.Websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Set<WebSocketSession>> salas = new ConcurrentHashMap<>();
    private final Map<String, UserInfo> sesiones = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket conectado: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = objectMapper.readTree(message.getPayload());
        String tipo = json.get("type").asText();

        switch (tipo) {
            case "join" -> handleJoin(session, json);
            case "offer" -> handleOffer(session, json);
            case "answer" -> handleAnswer(session, json);
            case "ice-candidate" -> handleIceCandidate(session, json);
            case "chat" -> handleChat(session, json);
            case "leave" -> handleLeave(session);
        }
    }

    private void handleJoin(WebSocketSession session, JsonNode json) throws IOException {
        String codigoReunion = json.get("room").asText();
        String peerId = json.get("peerId").asText();
        String nombre = json.get("nombre").asText();
        long usuarioId = json.get("usuarioId").asLong();

        UserInfo userInfo = new UserInfo(codigoReunion, peerId, nombre, usuarioId);
        sesiones.put(session.getId(), userInfo);
        salas.computeIfAbsent(codigoReunion, k -> new CopyOnWriteArraySet<>()).add(session);
        Set<WebSocketSession> participantes = salas.get(codigoReunion);

        ObjectNode joinNotification = objectMapper.createObjectNode();
        joinNotification.put("type", "user-joined");
        joinNotification.put("peerId", peerId);
        joinNotification.put("nombre", nombre);
        joinNotification.put("usuarioId", usuarioId);
        
        String joinMessage = objectMapper.writeValueAsString(joinNotification);
        
        for (WebSocketSession participante : participantes) {
            if (participante.isOpen() && !participante.getId().equals(session.getId())) {
                participante.sendMessage(new TextMessage(joinMessage));
            }
        }

        ObjectNode participantesInfo = objectMapper.createObjectNode();
        participantesInfo.put("type", "existing-participants");
        var participantesArray = participantesInfo.putArray("participants");
        
        for (WebSocketSession participante : participantes) {
            if (!participante.getId().equals(session.getId())) {
                UserInfo info = sesiones.get(participante.getId());
                if (info != null) {
                    var pNode = objectMapper.createObjectNode();
                    pNode.put("peerId", info.peerId);
                    pNode.put("nombre", info.nombre);
                    pNode.put("usuarioId", info.usuarioId);
                    participantesArray.add(pNode);
                }
            }
        }
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(participantesInfo)));
        
        System.out.println("Usuario " + nombre + " se unió a la sala " + codigoReunion + 
                          " (Total: " + participantes.size() + ")");
    }

    private void handleOffer(WebSocketSession session, JsonNode json) throws IOException {
        String targetPeerId = json.get("target").asText();
        forwardToPeer(session, json, targetPeerId);
    }

    private void handleAnswer(WebSocketSession session, JsonNode json) throws IOException {
        String targetPeerId = json.get("target").asText();
        forwardToPeer(session, json, targetPeerId);
    }

    private void handleIceCandidate(WebSocketSession session, JsonNode json) throws IOException {
        String targetPeerId = json.get("target").asText();
        forwardToPeer(session, json, targetPeerId);
    }

    private void handleChat(WebSocketSession session, JsonNode json) throws IOException {
        UserInfo senderInfo = sesiones.get(session.getId());
        if (senderInfo == null) return;

        Set<WebSocketSession> participantes = salas.get(senderInfo.codigoReunion);
        if (participantes == null) return;

        ObjectNode chatMessage = objectMapper.createObjectNode();
        chatMessage.put("type", "chat");
        chatMessage.put("nombre", senderInfo.nombre);
        chatMessage.put("texto", json.get("texto").asText());
        chatMessage.put("usuarioId", senderInfo.usuarioId);

        String message = objectMapper.writeValueAsString(chatMessage);
        
        for (WebSocketSession participante : participantes) {
            if (participante.isOpen() && !participante.getId().equals(session.getId())) {
                participante.sendMessage(new TextMessage(message));
            }
        }
    }

    private void handleLeave(WebSocketSession session) throws IOException {
        removeUserFromRoom(session);
    }

    private void forwardToPeer(WebSocketSession sender, JsonNode json, String targetPeerId) throws IOException {
        UserInfo senderInfo = sesiones.get(sender.getId());
        if (senderInfo == null) return;

        Set<WebSocketSession> participantes = salas.get(senderInfo.codigoReunion);
        if (participantes == null) return;

        ObjectNode forwardMessage = (ObjectNode) json;
        forwardMessage.put("from", senderInfo.peerId);
        forwardMessage.put("fromNombre", senderInfo.nombre);

        String message = objectMapper.writeValueAsString(forwardMessage);

        for (WebSocketSession participante : participantes) {
            UserInfo targetInfo = sesiones.get(participante.getId());
            if (targetInfo != null && targetInfo.peerId.equals(targetPeerId) && participante.isOpen()) {
                participante.sendMessage(new TextMessage(message));
                break;
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserFromRoom(session);
        System.out.println("WebSocket desconectado: " + session.getId());
    }

    private void removeUserFromRoom(WebSocketSession session) throws IOException {
        UserInfo userInfo = sesiones.remove(session.getId());
        if (userInfo == null) return;

        Set<WebSocketSession> participantes = salas.get(userInfo.codigoReunion);
        if (participantes != null) {
            participantes.remove(session);
            
            // Notificar a los demás que el usuario se fue
            ObjectNode leaveNotification = objectMapper.createObjectNode();
            leaveNotification.put("type", "user-left");
            leaveNotification.put("peerId", userInfo.peerId);
            leaveNotification.put("nombre", userInfo.nombre);
            leaveNotification.put("usuarioId", userInfo.usuarioId);

            String message = objectMapper.writeValueAsString(leaveNotification);
            
            for (WebSocketSession participante : participantes) {
                if (participante.isOpen()) {
                    participante.sendMessage(new TextMessage(message));
                }
            }
            if (participantes.isEmpty()) {
                salas.remove(userInfo.codigoReunion);
            }
            
            System.out.println("Usuario " + userInfo.nombre + " salió de la sala " + userInfo.codigoReunion);
        }
    }

    private static class UserInfo {
        String codigoReunion;
        String peerId;
        String nombre;
        long usuarioId;

        UserInfo(String codigoReunion, String peerId, String nombre, long usuarioId) {
            this.codigoReunion = codigoReunion;
            this.peerId = peerId;
            this.nombre = nombre;
            this.usuarioId = usuarioId;
        }
    }
}

