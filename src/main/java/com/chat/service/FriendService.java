
package com.chat.service;

import com.chat.model.Friend;
import com.chat.model.FriendGroup;
import com.chat.model.FriendRequest;
import com.chat.model.User;
import com.chat.repository.FriendGroupRepository;
import com.chat.repository.FriendRequestRepository;
import com.chat.repository.FriendRepository;
import com.chat.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendGroupRepository friendGroupRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendRepository friendRepository;

    public void createDefaultGroupIfNotExists(Long userId) {
        if (!friendGroupRepository.existsByUserIdAndGroupName(userId, "默认分组")) {
            FriendGroup group = new FriendGroup();
            group.setUserId(userId);
            group.setGroupName("默认分组");
            friendGroupRepository.save(group);
        }
    }

    public String sendFriendRequest(String senderUsername, String targetUsername) {
        Optional<User> sender = userRepository.findByUsername(senderUsername);
        Optional<User> receiver = userRepository.findByUsername(targetUsername);
        if (sender.isEmpty() || receiver.isEmpty()) return "用户不存在";
        if (sender.get().getId().equals(receiver.get().getId())) return "不能添加自己为好友";

        // 检查是否已是好友
        Long count = friendRepository.countFriendRelation(sender.get().getId(), receiver.get().getId());
        if (count != null && count > 0) return "你们已经是好友";


        FriendRequest request = new FriendRequest();
        request.setSenderId(sender.get().getId());
        request.setReceiverId(receiver.get().getId());
        request.setStatus("PENDING");
        friendRequestRepository.save(request);
        return "请求已发送";
    }

    public String acceptFriendRequest(Long requestId) {
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) return "请求不存在";

        FriendRequest request = requestOpt.get();
        request.setStatus("ACCEPTED");
        friendRequestRepository.save(request);

        Long senderId = request.getSenderId();
        Long receiverId = request.getReceiverId();

        // 确保双方都有默认分组
        createDefaultGroupIfNotExists(senderId);
        createDefaultGroupIfNotExists(receiverId);

        Long senderGroupId = friendGroupRepository.findByUserIdAndGroupName(senderId, "默认分组").get().getId();
        Long receiverGroupId = friendGroupRepository.findByUserIdAndGroupName(receiverId, "默认分组").get().getId();

        // 双向加好友
        friendRepository.insertFriend(senderId, receiverId, senderGroupId);
        friendRepository.insertFriend(receiverId, senderId, receiverGroupId);

        return "已添加为好友";
    }


    public List<Map<String, Object>> getPendingRequests(String receiverUsername) {
        Optional<User> receiver = userRepository.findByUsername(receiverUsername);
        if (receiver.isEmpty()) return Collections.emptyList();

        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(receiver.get().getId(), "PENDING");

        List<Map<String, Object>> result = new ArrayList<>();
        for (FriendRequest req : requests) {
            Optional<User> sender = userRepository.findById(req.getSenderId());
            sender.ifPresent(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", req.getId());
                map.put("senderUsername", user.getUsername());
                result.add(map);
            });
        }
        return result;
    }

    public String rejectFriendRequest(Long requestId) {
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) return "请求不存在";
        FriendRequest request = requestOpt.get();
        request.setStatus("REJECTED");
        friendRequestRepository.save(request);
        return "已拒绝";
    }

    public Map<String, List<String>> getFriendGroupsWithFriends(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Map.of();

        Long userId = userOpt.get().getId();

        List<FriendGroup> groups = friendGroupRepository.findByUserId(userId);
        Map<Long, String> groupIdToName = new HashMap<>();
        for (FriendGroup g : groups) {
            groupIdToName.put(g.getId(), g.getGroupName());
        }

        List<Friend> friends = friendRepository.findByUserId(userId);

        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Friend f : friends) {
            String groupName = groupIdToName.getOrDefault(f.getGroupId(), "未分组");
            userRepository.findById(f.getFriendId()).ifPresent(friendUser -> {
                result.computeIfAbsent(groupName, k -> new ArrayList<>()).add(friendUser.getUsername());
            });
        }
        return result;
    }

    public List<FriendGroup> getGroups(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(value -> friendGroupRepository.findByUserId(value.getId()))
                .orElse(Collections.emptyList());
    }


    public Map<String, List<String>> getFriendGroupMap(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Collections.emptyMap();

        Long userId = userOpt.get().getId();
        List<FriendGroup> groups = friendGroupRepository.findByUserId(userId);
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (FriendGroup group : groups) {
            List<User> friends = friendRepository.findFriendsByGroupId(group.getId());
            List<String> names = friends.stream().map(User::getUsername).toList();
            result.put(group.getGroupName(), names);  // 确保加的是用户名列表
        }

        return result;
    }

    public String moveFriendToGroup(String username, Long friendId, Long groupId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) return "用户不存在";

        int updated = friendRepository.updateFriendGroup(user.get().getId(), friendId, groupId);
        return updated > 0 ? "已移动" : "移动失败";
    }

    public String deleteFriend(String username, String friendUsername) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);
        if (userOpt.isEmpty() || friendOpt.isEmpty()) return "用户不存在";

        Long userId = userOpt.get().getId();
        Long friendId = friendOpt.get().getId();

        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);

        return "好友已删除";
    }


    public List<FriendGroup> getGroupListByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) return Collections.emptyList();
        return friendGroupRepository.findByUserId(user.get().getId());
    }

    public String moveFriendToGroup(String username, String friendUsername, String targetGroupName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);
        if (userOpt.isEmpty() || friendOpt.isEmpty()) return "用户不存在";

        Long userId = userOpt.get().getId();
        Long friendId = friendOpt.get().getId();

        // 获取或创建目标分组
        FriendGroup group = friendGroupRepository
                .findByUserIdAndGroupName(userId, targetGroupName)
                .orElseGet(() -> {
                    FriendGroup newGroup = new FriendGroup();
                    newGroup.setUserId(userId);
                    newGroup.setGroupName(targetGroupName);
                    return friendGroupRepository.save(newGroup);
                });

        // 更新好友所在分组
        int rowsUpdated = friendRepository.updateFriendGroup(userId, friendId, group.getId());
        if (rowsUpdated > 0) {
            return "移动成功";
        } else {
            return "移动失败";
        }
    }

    public List<String> getGroupNamesByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Collections.emptyList();

        List<FriendGroup> groups = friendGroupRepository.findByUserId(userOpt.get().getId());
        return groups.stream().map(FriendGroup::getGroupName).collect(Collectors.toList());
    }

    public Map<String, Object> getGroupNamesExcludingCurrent(String username, String friendUsername) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<User> friendOpt = userRepository.findByUsername(friendUsername);
        if (userOpt.isEmpty() || friendOpt.isEmpty()) return Map.of();

        Long userId = userOpt.get().getId();
        Long friendId = friendOpt.get().getId();

        // 查找当前好友的 groupId
        Optional<Friend> relation = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (relation.isEmpty()) return Map.of();

        Long currentGroupId = relation.get().getGroupId();
        Optional<FriendGroup> currentGroup = friendGroupRepository.findById(currentGroupId);
        String currentGroupName = currentGroup.map(FriendGroup::getGroupName).orElse("");

        List<String> allGroupNames = friendGroupRepository.findByUserId(userId).stream()
                .map(FriendGroup::getGroupName)
                .collect(Collectors.toList());

        return Map.of(
                "currentGroup", currentGroupName,
                "groups", allGroupNames
        );
    }





}
