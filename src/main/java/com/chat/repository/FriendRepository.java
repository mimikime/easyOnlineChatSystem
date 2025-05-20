package com.chat.repository;

import com.chat.model.Friend;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(value = "SELECT COUNT(*) FROM friends WHERE user_id = ?1 AND friend_id = ?2", nativeQuery = true)
    Long countFriendRelation(Long userId, Long friendId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friends (user_id, friend_id, group_id) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void insertFriend(Long userId, Long friendId, Long groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM friends WHERE user_id = ?1 AND friend_id = ?2", nativeQuery = true)
    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
