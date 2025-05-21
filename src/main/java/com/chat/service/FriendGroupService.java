package com.chat.service;

import com.chat.model.FriendGroup;
import com.chat.repository.FriendGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendGroupService {

    @Autowired
    private FriendGroupRepository friendGroupRepository;

    public boolean createGroup(Long userId, String groupName) {
        boolean exists = friendGroupRepository.existsByUserIdAndGroupName(userId, groupName);
        if (exists) return false;

        FriendGroup group = new FriendGroup();
        group.setUserId(userId);
        group.setGroupName(groupName);
        friendGroupRepository.save(group);
        return true;
    }
}
