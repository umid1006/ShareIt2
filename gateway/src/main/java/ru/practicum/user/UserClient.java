package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.UserDto;

import java.util.Map;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/gateway/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
        log.info("UserClient initialized for server: {}", serverUrl);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        log.info("Sending POST request to server for user: {}", userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto userDto) {
        log.info("Updating user {} with data: {}", userId, userDto);
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUserById(long userId, long targetUserId) {
        log.info("Fetching user {} by userId: {}", targetUserId, userId);
        return get("/" + targetUserId, userId);
    }

    public ResponseEntity<Object> getAllUsers(long userId, Integer from, Integer size) {
        log.info("Fetching all users by userId: {}", userId);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> deleteUser(long userId, long targetUserId) {
        log.info("Deleting user {} by userId: {}", targetUserId, userId);
        return delete("/" + targetUserId, userId);
    }
}