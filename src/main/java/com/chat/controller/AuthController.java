package com.chat.controller;

import com.chat.model.User;
import com.chat.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // 处理注册提交
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setApproved(false); // 管理员审核后才可登录
        userRepository.save(user);

        model.addAttribute("msg", "注册成功，等待管理员审核");
        return "login";
    }

    // 显示登录页面
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 登录成功处理（替换旧的 login 方法）
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }

        if (!user.isApproved()) {
            model.addAttribute("error", "账号未审核");
            return "login";
        }

        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);

        session.setAttribute("loginUser", user);
        return "redirect:/chat";
    }

    @GetMapping("/chat")
    public String showChatPage() {
        return "chat"; // 会自动渲染 templates/chat.html
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


}
