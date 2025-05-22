package com.chat.service;

import com.chat.model.Message;
import com.chat.model.User;
import com.chat.repository.MessageRepository;
import com.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public String sendPrivateMessage(String fromUsername, String toUsername, String content) {
        Optional<User> from = userRepository.findByUsername(fromUsername);
        Optional<User> to = userRepository.findByUsername(toUsername);

        if (from.isEmpty() || to.isEmpty()) return "用户不存在";

        Message message = new Message();
        message.setSenderId(from.get().getId());
        message.setReceiverId(to.get().getId());
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
        return "发送成功";
    }

    public List<Message> getPrivateChatHistory(String user1, String user2) {
        Long id1 = userRepository.findByUsername(user1).map(User::getId).orElse(null);
        Long id2 = userRepository.findByUsername(user2).map(User::getId).orElse(null);
        if (id1 == null || id2 == null) return Collections.emptyList();

        List<Message> messages = messageRepository.findPrivateMessages(id1, id2);

        for (Message msg : messages) {
            userRepository.findById(msg.getSenderId()).ifPresent(sender ->
                    msg.setSenderUsername(sender.getUsername())
            );
        }

        return messages;
    }

    public String sendGroupMessage(String fromUsername, Long groupId, String content) {
        Optional<User> senderOpt = userRepository.findByUsername(fromUsername);
        if (senderOpt.isEmpty()) return "发送者不存在";

        Message msg = new Message();
        msg.setSenderId(senderOpt.get().getId());
        msg.setGroupId(groupId);
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());

        messageRepository.save(msg);
        return "群消息发送成功";
    }

    public List<Message> getGroupChatHistory(Long groupId) {
        List<Message> messages = messageRepository.findByGroupIdOrderByTimestampAsc(groupId);

        for (Message msg : messages) {
            userRepository.findById(msg.getSenderId()).ifPresent(sender ->
                    msg.setSenderUsername(sender.getUsername())
            );
        }

        return messages;
    }
}
