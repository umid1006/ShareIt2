package ru.practicum.itemrequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/gateway/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> {
                            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
                            factory.setConnectTimeout(5000); // Таймаут подключения 5 сек
                            factory.setReadTimeout(5000);    // Таймаут чтения 5 сек
                            return factory;
                        })
                        .build()
        );
        log.info("ItemRequestClient initialized for server: {}", serverUrl);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        log.info("Creating item request for user {}", userId);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        log.info("Fetching request {} for user {}", requestId, userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        log.info("Fetching all requests for user {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Fetching all requests (from={}, size={}) for user {}", from, size, userId);

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> cancelRequest(Long userId, Long requestId) {
        log.info("Cancelling request {} for user {}", requestId, userId);
        return delete("/" + requestId, userId);
    }
}