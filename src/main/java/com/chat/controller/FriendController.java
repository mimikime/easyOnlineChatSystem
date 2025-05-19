
package com.chat.controller;

import com.chat.model.Friend;
import com.chat.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendRepository friendRepo;

    // 发送好友请求
    @PostMapping("/add")
    public String addFriend(@RequestParam String user,
                            @RequestParam String friend) {
        Friend f = new Friend();
        f.setUser(user);
        f.setFriend(friend);
        f.setStatus("pending");
        f.setGroupName("默认分组");
        friendRepo.save(f);
        return "好友请求已发送";
    }

    // 接收者查看待审核请求
    @GetMapping("/requests")
    public List<Friend> pendingRequests(@RequestParam String username) {
        return friendRepo.findByFriendAndStatus(username, "pending");
    }

    // 接受好友请求
    @PostMapping("/approve")
    public String approve(@RequestParam Long requestId) {
        Friend f = friendRepo.findById(requestId).orElse(null);
        if (f != null) {
            f.setStatus("accepted");
            friendRepo.save(f);
            return "已通过请求";
        }
        return "请求不存在";
    }

    // 显示我的好友
    @GetMapping("/list")
    public List<Friend> listMyFriends(@RequestParam String user) {
        return friendRepo.findByUser(user).stream()
                .filter(f -> "accepted".equals(f.getStatus()))
                .toList();
    }

    // 删除好友
    @DeleteMapping("/delete")
    public String delete(@RequestParam Long id) {
        friendRepo.deleteById(id);
        return "已删除";
    }

    // 移动分组
    @PostMapping("/move")
    public String move(@RequestParam Long id, @RequestParam String newGroup) {
        Friend f = friendRepo.findById(id).orElse(null);
        if (f != null) {
            f.setGroupName(newGroup);
            friendRepo.save(f);
            return "已移动到分组：" + newGroup;
        }
        return "好友不存在";
    }

    // 重发验证请求
    @PostMapping("/resend")
    public String resend(@RequestParam Long id) {
        Friend f = friendRepo.findById(id).orElse(null);
        if (f != null && !"accepted".equals(f.getStatus())) {
            f.setStatus("pending");
            friendRepo.save(f);
            return "已重新发送验证请求";
        }
        return "无法重新发送";
    }
}
