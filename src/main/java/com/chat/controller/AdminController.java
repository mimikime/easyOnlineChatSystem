package com.chat.controller;

import com.chat.model.User;
import com.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // 审核通过
    @GetMapping("/approve/{username}")
    @ResponseBody
    public String approveUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return "用户不存在";

        user.setApproved(true);
        userRepository.save(user);
        return "用户已审核通过";
    }

    // 查看所有未审核用户（可选）
    @GetMapping("/pending")
    @ResponseBody
    public String listPendingUsers() {
        StringBuilder sb = new StringBuilder();
        userRepository.findAll().stream()
                .filter(u -> !u.isApproved())
                .forEach(u -> sb.append(u.getUsername()).append("<br>"));
        return sb.toString();
    }
}
