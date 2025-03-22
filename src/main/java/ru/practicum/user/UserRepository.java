// UserRepository.java
package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @NonNull // Apply NonNull to overridden methods
    Optional<User> findById(@NonNull Long ownerId); // Keep parameter name consistent
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @NonNull
    List<User> findAll();
}