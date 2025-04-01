package ru.practicum.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor // Use Lombok for constructor injection
public class ValidationService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public void checkUniqueEmailToUpdate(User user) {
        if (user.getId() == null) {
            throw new NotFoundException("Обновление пользователя невозможно, ID не может быть null.");
        }

        // Use Spring Data JPA's built-in methods for better efficiency and clarity
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new ConflictException(String.format("Пользователь с email %s уже существует.", user.getEmail()));
        }
    }

    public void checkUniqueEmailToCreate(User user) {
        // Use Spring Data JPA's built-in methods
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException(String.format("Пользователь с email %s уже существует.", user.getEmail()));
        }
    }

    public void validateUserFields(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("Email пользователя не может быть пустым.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidateException("Имя пользователя не может быть пустым.");
        }
    }

    public boolean[] checkFieldsForUpdate(User user) {
        boolean[] result = new boolean[2];
        result[0] = user.getName() != null && !user.getName().isBlank();
        result[1] = user.getEmail() != null && !user.getEmail().isBlank();

        if (!result[0] && !result[1]) { // More efficient check
            throw new ValidateException("Все поля пользователя равны 'null'.");
        }
        return result;
    }

    public User checkExistUserInDB(Long userId) {
        // Use orElseThrow for concise error handling
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден."));
    }

    public Item checkExistItemInDB(Long itemId) {

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
    }

    public void checkMissingItemInDB(Long itemId) {
        // Use existsById for efficiency
        if (itemRepository.existsById(itemId)) {
            throw new ConflictException("Вещь с ID = " + itemId + " уже существует.");
        }
    }

    public void validateItemFields(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidateException("Название вещи не может быть пустым.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidateException("Описание вещи не может быть пустым.");
        }
        if (item.getAvailable() == null) {
            throw new ValidateException("Для вещи необходим статус её бронирования.");
        }
        if (item.getOwner() == null || item.getOwner().getId() == null) {
            throw new ValidateException("Для вещи необходим хозяин.");
        }
    }

    public boolean[] checkFieldsForUpdate(Item item) {
        boolean[] result = new boolean[3];
        result[0] = item.getName() != null && !item.getName().isBlank();
        result[1] = item.getDescription() != null && !item.getDescription().isBlank();
        result[2] = item.getAvailable() != null;

        // More efficient check:  Are *all* fields false?
        if (!result[0] && !result[1] && !result[2]) {
            throw new ValidateException("Все поля: название, описание и статус доступа к аренде равны 'null'.");
        }
        return result;
    }

    public boolean isOwnerItem(Item item, Long ownerId) {
        if (item == null || ownerId == null) {
            throw new ValidateException("Вещь и (или) ID хозяина вещи равны null.");
        }

        if (!ownerId.equals(item.getOwner().getId())) {
            String message = String.format("Вещь %s не принадлежит хозяину с ID = %d.", item.getName(), ownerId);
            throw new NotFoundException(message);
        }
        return true;
    }
}