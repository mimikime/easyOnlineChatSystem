package com.chat.controller;

import com.chat.model.FriendGroup;
import com.chat.service.FriendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/request")
    public String request(@RequestParam String targetUsername, HttpSession session) {
        String sender = (String) session.getAttribute("username");
        return friendService.sendFriendRequest(sender, targetUsername);
    }

    @PostMapping("/accept")
    public String accept(@RequestParam Long requestId) {
        return friendService.acceptFriendRequest(requestId);
    }


    @GetMapping("/pending")
    public List<Map<String, Object>> pending(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return Collections.emptyList();
        return friendService.getPendingRequests(username);
    }


    @PostMapping("/reject")
    public String reject(@RequestParam Long requestId) {
        return friendService.rejectFriendRequest(requestId);
    }

    @GetMapping("/groups")
    @ResponseBody
    public Map<String, List<String>> getFriendGroups(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return Collections.emptyMap();

        return friendService.getFriendGroupMap(username); // 返回 Map<String, List<String>>
    }


    @GetMapping("/group/list")
    @ResponseBody
    public List<FriendGroup> listGroups(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return friendService.getGroups(username);  // 返回 List<FriendGroup>
    }


    @PostMapping("/delete")
    public String delete(@RequestParam String friendUsername, HttpSession session) {
        String username = (String) session.getAttribute("username");
        return friendService.deleteFriend(username, friendUsername);
    }


    @PostMapping("/move")
    public String moveFriendToGroup(@RequestParam String friendUsername,
                                    @RequestParam String targetGroupName,
                                    HttpSession session) {
        String username = (String) session.getAttribute("username");
        return friendService.moveFriendToGroup(username, friendUsername, targetGroupName);
    }

    @GetMapping("/group/names")
    public Map<String, Object> getGroupNames(@RequestParam String friendUsername, HttpSession session) {
        String username = (String) session.getAttribute("username");
        return friendService.getGroupNamesExcludingCurrent(username, friendUsername);
    }

    @GetMapping("/deletion-notices")
    public List<String> getDeletionNotices(HttpSession session) {
        List<String> notices = (List<String>) session.getAttribute("deletionNotices");
        if (notices == null) return Collections.emptyList();
        session.removeAttribute("deletionNotices");
        return notices;
    }
}
