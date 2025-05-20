
package com.chat.controller;

import com.chat.service.FriendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/delete")
    public String delete(@RequestParam String friendUsername, HttpSession session) {
        String username = (String) session.getAttribute("username");
        return friendService.deleteFriend(username, friendUsername);
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

}
