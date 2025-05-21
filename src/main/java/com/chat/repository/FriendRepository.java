package com.chat.repository;

import com.chat.model.Friend;
import com.chat.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    @Query(value = "SELECT * FROM friends WHERE user_id = ?1", nativeQuery = true)
    List<Friend> findByUserId(Long userId);


    @Modifying
    @Transactional
    @Query("SELECT u FROM User u JOIN Friend f ON u.id = f.friendId WHERE f.groupId = :groupId")
    List<User> findFriendsByGroupId(@Param("groupId") Long groupId);


    @Modifying
    @Transactional
    @Query("DELETE FROM Friend f WHERE f.userId = ?1 AND f.friendId = ?2")
    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE friends SET group_id = ?3 WHERE user_id = ?1 AND friend_id = ?2", nativeQuery = true)
    int updateFriendGroup(Long userId, Long friendId, Long newGroupId);

    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);


}
