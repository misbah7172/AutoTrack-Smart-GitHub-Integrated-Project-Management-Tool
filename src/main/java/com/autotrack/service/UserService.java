package com.autotrack.service;

import com.autotrack.model.Role;
import com.autotrack.model.User;
import com.autotrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for user management and OAuth2 authentication.
 */
@Service
public class UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * Get user by GitHub ID.
     */
    public User getUserByGitHubId(String gitHubId) {
        return userRepository.findByGitHubId(gitHubId)
                .orElseThrow(() -> new RuntimeException("User not found with GitHub ID: " + gitHubId));
    }

    /**
     * Get user by email.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Get user by nickname.
     */
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("User not found with nickname: " + nickname));
    }

    /**
     * Process OAuth2 login and create/update user.
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        // Extract user attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String gitHubId = attributes.get("id").toString();
        String nickname = attributes.get("login").toString();
        String email = (String) attributes.get("email");
        String avatarUrl = (String) attributes.get("avatar_url");
        
        // Find or create user
        User user = userRepository.findByGitHubId(gitHubId)
                .orElseGet(() -> createUser(gitHubId, nickname, email, avatarUrl));
        
        // Update user information
        user.setNickname(nickname);
        if (email != null) {
            user.setEmail(email);
        }
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        
        // Create authorities
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        
        // Return OAuth2User with user authorities
        return new DefaultOAuth2User(
                authorities,
                attributes,
                "login"
        );
    }

    /**
     * Create a new user.
     */
    @Transactional
    public User createUser(String gitHubId, String nickname, String email, String avatarUrl) {
        User user = User.builder()
                .gitHubId(gitHubId)
                .nickname(nickname)
                .email(email)
                .avatarUrl(avatarUrl)
                .roles(Collections.singletonList(Role.MEMBER))
                .build();
        
        return userRepository.save(user);
    }

    /**
     * Update a user's role.
     */
    @Transactional
    public User updateUserRole(Long userId, List<Role> roles) {
        User user = getUserById(userId);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    /**
     * Get current user from OAuth2User.
     */
    public User getCurrentUser(OAuth2User principal) {
        String gitHubId = principal.getAttribute("id").toString();
        return getUserByGitHubId(gitHubId);
    }
}
