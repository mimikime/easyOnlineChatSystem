package com.chat.handler;

import com.chat.model.ChatMessage;
import com.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    public static ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private ChatMessageRepository messageRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = session.getUri().getQuery().split("=")[1];
        sessions.put(username, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端发来 JSON：{from, to, content}
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(payload);

        String from = json.get("from").asText();
        String to = json.get("to").isNull() ? null : json.get("to").asText();
        String content = json.get("content").asText();

        // 保存到数据库
        ChatMessage msg = new ChatMessage();
        msg.setSender(from);
        msg.setReceiver(to);
        msg.setMessage(content);
        messageRepository.save(msg);

        String formatted = from + (to == null ? "（群聊）" : " → " + to) + ": " + content;

        if (to == null) {
            // 群聊：广播给所有人
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(formatted));
                }
            }
        } else {
            // 私聊：只发给目标和自己
            WebSocketSession toSession = sessions.get(to);
            if (toSession != null && toSession.isOpen()) {
                toSession.sendMessage(new TextMessage(formatted));
            }
            if (!from.equals(to)) {
                session.sendMessage(new TextMessage(formatted));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().remove(session);
    }
}
