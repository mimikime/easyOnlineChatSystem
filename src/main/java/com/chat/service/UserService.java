package com.chat.service;

import com.chat.model.User;
import com.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;  // 用户名已存在
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // 密码加密
        userRepository.save(user);
        return true;
    }

    public boolean login(User user) {
        return userRepository.findByUsername(user.getUsername())
                .map(existingUser -> passwordEncoder.matches(user.getPassword(), existingUser.getPassword()))
                .orElse(false);
    }
}
