
package com.chat.controller;

import com.chat.model.ChatMessage;
import com.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository messageRepository;

    @GetMapping("/history")
    @ResponseBody
    public List<ChatMessage> getHistory(@RequestParam String username) {
        return messageRepository.findBySenderOrReceiver(username, username);
    }

    @GetMapping("/download")
    public void download(@RequestParam String username, HttpServletResponse response) throws Exception {
        List<ChatMessage> messages = messageRepository.findBySenderOrReceiver(username, username);

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=chat_history.txt");

        PrintWriter writer = response.getWriter();
        for (ChatMessage msg : messages) {
            writer.println("[" + msg.getTimestamp() + "] " + msg.getSender() + " → " + (msg.getReceiver() == null ? "群聊" : msg.getReceiver()) + ": " + msg.getMessage());
        }
        writer.flush();
        writer.close();
    }
}
