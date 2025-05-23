package com.chat.repository;

import com.chat.model.Group;
import com.chat.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByMemberId(Long memberId);
    @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.groupId WHERE gm.memberId = :memberId")
    List<Group> findGroupsByMemberId(@Param("memberId") Long memberId);

}
