package com.chat.controller;

import com.chat.model.Group;
import com.chat.model.User;
import com.chat.repository.UserRepository;
import com.chat.service.FriendService;
import com.chat.service.GroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "请先登录";

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return "用户不存在";

        boolean created = groupService.createGroup(groupName, user.getId());
        return created ? "群组创建成功" : "群组名已存在";
    }

    @GetMapping("/list")
    public List<Group> listGroups(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return Collections.emptyList();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        return groupService.findGroupsByUserId(user.getId());
    }

    @GetMapping("/groups")
    public Map<String, List<String>> getGroups(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return Collections.emptyMap();

        return friendService.getFriendGroupsWithFriends(username);
    }

}
