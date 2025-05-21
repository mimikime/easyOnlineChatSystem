package com.chat.repository;

import com.chat.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByMemberId(Long memberId);
    List<GroupMember> findByGroupId(Long groupId);
}
