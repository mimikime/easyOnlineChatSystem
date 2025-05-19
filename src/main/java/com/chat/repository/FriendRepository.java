
package com.chat.repository;

import com.chat.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUser(String user);
    List<Friend> findByFriendAndStatus(String friend, String status);
}
