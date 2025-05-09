// DtoJsonTest.java
package ru.practicum.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.dto.ItemRequestWithItemsDto;
import ru.practicum.dto.ItemResponseDto;

import jakarta.validation.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestJson;

    @Autowired
    private JacksonTester<ItemRequestWithItemsDto> itemRequestWithItemsJson;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testItemRequestDtoSerialization() throws IOException {
        ItemRequestDto dto = new ItemRequestDto("Need a new laptop");

        JsonContent<ItemRequestDto> result = itemRequestJson.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a new laptop");
    }

    @Test
    void testItemRequestDtoDeserialization() throws IOException {
        String content = "{\"description\":\"Need a new laptop\"}";

        ItemRequestDto result = itemRequestJson.parseObject(content);

        assertThat(result.getDescription()).isEqualTo("Need a new laptop");
    }

    @Test
    void testItemRequestDtoValidation() {
        ItemRequestDto validDto = new ItemRequestDto("Valid description");
        ItemRequestDto invalidDto = new ItemRequestDto("");

        Set<ConstraintViolation<ItemRequestDto>> validViolations = validator.validate(validDto);
        Set<ConstraintViolation<ItemRequestDto>> invalidViolations = validator.validate(invalidDto);

        assertThat(validViolations).isEmpty();
        assertThat(invalidViolations).hasSize(1);
        assertThat(invalidViolations.iterator().next().getMessage())
                .isEqualTo("Описание запроса не может быть пустым");
    }

    @Test
    void testItemRequestWithItemsDtoSerialization() throws IOException {
        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .ownerId(2L)
                .build();

        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();
        dto.setId(1L);
        dto.setDescription("Need a new laptop");
        dto.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));
        dto.setItems(List.of(itemDto));

        JsonContent<ItemRequestWithItemsDto> result = itemRequestWithItemsJson.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a new laptop");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Laptop");
    }

    @Test
    void testItemRequestWithItemsDtoDeserialization() throws IOException {
        String content = "{\"id\":1,\"description\":\"Need a new laptop\"," +
                "\"created\":\"2023-01-01T12:00:00\"," +
                "\"items\":[{\"id\":1,\"name\":\"Laptop\",\"ownerId\":2}]}";

        ItemRequestWithItemsDto result = itemRequestWithItemsJson.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Need a new laptop");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getId()).isEqualTo(1L);
        assertThat(result.getItems().getFirst().getName()).isEqualTo("Laptop");
        assertThat(result.getItems().getFirst().getOwnerId()).isEqualTo(2L);
    }

    @Test
    void testItemRequestWithItemsDtoWithEmptyItems() throws IOException {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();
        dto.setId(1L);
        dto.setDescription("Empty items list");
        dto.setCreated(LocalDateTime.now());
        dto.setItems(Collections.emptyList());

        JsonContent<ItemRequestWithItemsDto> result = itemRequestWithItemsJson.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}