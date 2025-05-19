
package com.chat.model;

import jakarta.persistence.*;

@Entity
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;        // 申请人
    private String friend;      // 被申请人
    private String groupName;   // 所属分组
    private String status;      // pending / accepted / rejected

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getFriend() { return friend; }
    public void setFriend(String friend) { this.friend = friend; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
