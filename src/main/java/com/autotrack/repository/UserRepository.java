package com.autotrack.repository;

import com.autotrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByGitHubId(String gitHubId);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByNickname(String nickname);
}
