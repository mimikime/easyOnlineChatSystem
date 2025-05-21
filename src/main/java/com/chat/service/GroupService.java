package com.chat.service;

import com.chat.model.Group;
import com.chat.model.GroupMember;
import com.chat.repository.GroupMemberRepository;
import com.chat.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

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


}
