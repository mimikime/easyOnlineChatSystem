package com.chat.controller;

import com.chat.model.Message;
import com.chat.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 发送私聊消息
    @PostMapping("/send")
    public String sendPrivate(@RequestParam String to,
                              @RequestParam String content,
                              HttpSession session) {
        String from = (String) session.getAttribute("username");
        if (from == null) return "未登录";

        return messageService.sendPrivateMessage(from, to, content);
    }

    // 获取与某人的聊天记录
    @GetMapping("/history")
    public List<Message> getPrivateHistory(@RequestParam String withUser,
                                           HttpSession session) {
        String me = (String) session.getAttribute("username");
        return messageService.getPrivateChatHistory(me, withUser);
    }

    // 发送群消息
    @PostMapping("/group/send")
    public String sendGroup(@RequestParam Long groupId,
                            @RequestParam String content,
                            HttpSession session) {
        String from = (String) session.getAttribute("username");
        if (from == null) return "未登录";

        return messageService.sendGroupMessage(from, groupId, content);
    }

    // 获取某群的聊天记录
    @GetMapping("/group/history")
    public List<Message> getGroupHistory(@RequestParam Long groupId) {
        return messageService.getGroupChatHistory(groupId);
    }
}
