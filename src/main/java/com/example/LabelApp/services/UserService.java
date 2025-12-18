package com.example.LabelApp.services;

import com.example.LabelApp.dto.UserRegistrationDto;
import com.example.LabelApp.models.Track;
import com.example.LabelApp.models.User;
import com.example.LabelApp.models.enums.Role;
import com.example.LabelApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(User user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) {
            return false;
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        log.info("Saving new user with email {}", email);
        userRepository.save(user);
        return true;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public void changeUserRoles(User user, String[] rolesFromForm) {
        user.getRoles().clear();

        if (rolesFromForm != null) {
            for (String roleName : rolesFromForm) {
                if (roleName.startsWith("ROLE_")) {
                    user.getRoles().add(Role.valueOf(roleName));
                }
            }
        }

        if (user.getRoles().isEmpty()) {
            user.getRoles().add(Role.ROLE_USER);
        }

        userRepository.save(user);
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(!user.isActive());
            log.info("User {} (id={}) is now {}", user.getEmail(), user.getId(), user.isActive() ? "active" : "banned");
            userRepository.save(user);
        }
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean createUserFromDto(UserRegistrationDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return createUser(user);
    }

    // ==================== НОВЫЕ МЕТОДЫ ДЛЯ ИЗБРАННОГО ====================

    /**
     * Добавляет трек в избранное пользователя
     */
    @Transactional
    public void addToFavorites(User user, Track track) {
        if (user == null || track == null) {
            throw new IllegalArgumentException("User or Track cannot be null");
        }

        boolean added = user.getFavorites().add(track);
        if (added) {
            userRepository.save(user);
            log.info("Track {} added to favorites of user {}", track.getId(), user.getId());
        }
    }

    /**
     * Удаляет трек из избранного пользователя
     */
    @Transactional
    public void removeFromFavorites(User user, Track track) {
        if (user == null || track == null) {
            throw new IllegalArgumentException("User or Track cannot be null");
        }

        boolean removed = user.getFavorites().remove(track);
        if (removed) {
            userRepository.save(user);
            log.info("Track {} removed from favorites of user {}", track.getId(), user.getId());
        }
    }
}