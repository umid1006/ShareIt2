// ItemRepository.java
package ru.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId); // Correct

    // Correct query for searching (case-insensitive)
    @Query("SELECT i FROM Item i WHERE " +
            "(UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) OR " +
            "UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> search(@Param("text") String text);
}