
package com.chat.repository;

import com.chat.model.Friend;
import com.chat.model.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendGroupRepository extends JpaRepository<FriendGroup, Long> {

    boolean existsByUserIdAndGroupName(Long userId, String groupName);

    Optional<FriendGroup> findByUserIdAndGroupName(Long userId, String groupName);

    boolean existsByGroupName(String groupName);

    List<FriendGroup> findByUserId(Long userId);



}
