// UserServiceImplIT.java (Integration Tests)
package ru.practicum.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserServiceImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);

        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        em.persist(user1);

        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        em.persist(user2);
    }

    @Test
    void saveUser_shouldSaveNewUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@example.com");

        User savedUser = userService.saveUser(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("New User", savedUser.getName());
        assertEquals("new@example.com", savedUser.getEmail());

        User fromDb = em.find(User.class, savedUser.getId());
        assertEquals(savedUser.getName(), fromDb.getName());
    }

    @Test
    void saveUser_shouldThrowWhenEmailExists() {
        User duplicateEmailUser = new User();
        duplicateEmailUser.setName("Duplicate");
        duplicateEmailUser.setEmail("user1@example.com"); // Существующий email

        assertThrows(DuplicateEmailException.class, () -> userService.saveUser(duplicateEmailUser));
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        User updateData = new User();
        updateData.setName("Updated Name");
        updateData.setEmail("updated@example.com");

        User updatedUser = userService.updateUser(updateData, user1.getId());

        assertEquals(user1.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailNotProvided() {
        User updateData = new User();
        updateData.setName("Updated Name Only");

        User updatedUser = userService.updateUser(updateData, user1.getId());

        assertEquals(user1.getId(), updatedUser.getId());
        assertEquals("Updated Name Only", updatedUser.getName());
        assertEquals("user1@example.com", updatedUser.getEmail()); // Email остался прежним
    }

    @Test
    void updateUser_shouldThrowWhenEmailExists() {
        User updateData = new User();
        updateData.setEmail("user2@example.com"); // Email другого пользователя

        assertThrows(DuplicateEmailException.class, () -> userService.updateUser(updateData, user1.getId()));
    }

    @Test
    void updateUser_shouldThrowWhenUserNotFound() {
        User updateData = new User();
        updateData.setName("Not Found");

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateData, 999L));
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        User foundUser = userService.getUserById(user1.getId());

        assertEquals(user1.getId(), foundUser.getId());
        assertEquals("User One", foundUser.getName());
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() {
        em.remove(user1);
        em.remove(user2);

        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        userService.deleteUser(user1.getId());

        assertNull(em.find(User.class, user1.getId()));
        assertNotNull(em.find(User.class, user2.getId())); // Другой пользователь остался
    }

    @Test
    void deleteUser_shouldThrowWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }
}