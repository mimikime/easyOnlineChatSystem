package com.chat.repository;

import com.chat.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);

    Optional<Group> findByName(String name);

}
