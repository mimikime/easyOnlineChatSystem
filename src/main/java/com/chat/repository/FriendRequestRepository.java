package com.chat.repository;

import com.chat.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, String status);
}
