//user/UserService.java
package ru.practicum.user;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    User updateUser(User user, Long userId);
    User getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);
}