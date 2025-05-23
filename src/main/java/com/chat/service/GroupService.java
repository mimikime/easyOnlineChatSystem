package com.chat.service;

import com.chat.model.Group;
import com.chat.model.GroupMember;
import com.chat.model.User;
import com.chat.repository.GroupMemberRepository;
import com.chat.repository.GroupRepository;
import com.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;


    public boolean createGroup(String groupName, Long creatorId) {
        if (groupRepository.existsByName(groupName)) {
            return false;
        }

        Group group = new Group();
        group.setName(groupName);
        group.setCreatorId(creatorId);
        groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setMemberId(creatorId);
        groupMemberRepository.save(member);

        return true;
    }

    public List<Group> findGroupsByUserId(Long userId) {
        List<GroupMember> memberships = groupMemberRepository.findByMemberId(userId);
        return memberships.stream()
                .map(m -> groupRepository.findById(m.getGroupId()).orElse(null))
                .filter(g -> g != null)
                .toList();
    }

    public List<Group> getGroupsByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return List.of();
        return groupMemberRepository.findGroupsByMemberId(userOpt.get().getId());
    }

    public boolean joinGroupByName(String username, String groupName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Group> groupOpt = groupRepository.findByName(groupName);

        if (userOpt.isEmpty() || groupOpt.isEmpty()) return false;

        Long userId = userOpt.get().getId();
        Long groupId = groupOpt.get().getId();



        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setMemberId(userId);
        groupMemberRepository.save(member);

        return true;
    }
}
