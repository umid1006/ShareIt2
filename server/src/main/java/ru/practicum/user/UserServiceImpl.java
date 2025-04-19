// user/UserServiceImpl.java
package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists.");
        }
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found."));

        // WORKAROUND: Only update email if a valid email is provided
        if (StringUtils.hasText(user.getEmail())) {
            if (!user.getEmail().equalsIgnoreCase(existingUser.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
                throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists.");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) { // Correct null check for name
            existingUser.setName(user.getName());
        }

        return userRepository.save(existingUser);
    }


    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) { // More efficient check
            throw new NotFoundException("User with ID " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }
}