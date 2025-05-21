package com.chat.controller;

import com.chat.model.User;
import com.chat.repository.UserRepository;
import com.chat.service.FriendGroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friend/group")
public class FriendGroupController {

    @Autowired
    private FriendGroupService friendGroupService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "请先登录";
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return "用户不存在";

        boolean success = friendGroupService.createGroup(user.getId(), groupName);
        return success ? "分组创建成功" : "分组名已存在";
    }
}
