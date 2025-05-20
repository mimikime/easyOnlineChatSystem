
package com.chat.repository;

import com.chat.model.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendGroupRepository extends JpaRepository<FriendGroup, Long> {
    boolean existsByUserIdAndGroupName(Long userId, String groupName);
    Optional<FriendGroup> findByUserIdAndGroupName(Long userId, String groupName);
}
