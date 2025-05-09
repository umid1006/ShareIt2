package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/gateway/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> {
                            HttpComponentsClientHttpRequestFactory factory =
                                    new HttpComponentsClientHttpRequestFactory();
                            factory.setHttpClient(HttpClients.createDefault());
                            return factory;
                        })
                        .build()
        );
        log.info("ItemClient initialized for server: {}", serverUrl);
    }

    public ResponseEntity<Object> addItem(long userId, @Valid ItemDto itemDto) {
        log.info("Creating item for user {}: {}", userId, itemDto);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info("Updating item {} for user {}", itemId, userId);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(long itemId) {
        log.info("Fetching item {}", itemId);
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getUserItems(long userId, int from, int size) {
        log.info("Fetching items for user {}, from={}, size={}", userId, from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(String text, int from, int size) {
        log.info("Searching items by text '{}', from={}, size={}", text, from, size);
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, @Valid CommentDto commentDto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}