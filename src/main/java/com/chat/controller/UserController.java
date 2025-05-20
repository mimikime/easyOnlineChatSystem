package com.chat.controller;

import com.chat.model.User;
import com.chat.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        boolean success = userService.register(user);
        if (success) {
            model.addAttribute("msg", "注册成功，请登录");
            return "login";
        } else {
            model.addAttribute("error", "用户名已存在，请换一个！");
            return "register";
        }
    }

    @PostMapping("/auth/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        boolean success = userService.login(user);
        if (success) {
            session.setAttribute("username", username); // 存入 session
            return "redirect:/chat"; // 使用 redirect 避免表单重复提交
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    @GetMapping("/chat")
    public String chat(Model model, HttpSession session) {
        Object username = session.getAttribute("username");
        if (username == null) {
            return "redirect:/login"; // 未登录跳回登录页
        }
        model.addAttribute("username", username);
        return "chat";
    }

}
