package com.chat.handler;

import com.chat.model.ChatMessage;
import com.chat.repository.ChatMessageRepository;
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
        String payload = message.getPayload(); // 格式: 张三: 哈喽大家好
        if (!payload.contains(":")) return;

        String[] parts = payload.split(":", 2);
        String sender = parts[0].trim();
        String content = parts[1].trim();

        // 群聊（receiver=null）
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setMessage(content);
        chatMessage.setReceiver(null); // 群聊标识
        messageRepository.save(chatMessage);

        // 广播
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().remove(session);
    }
}
