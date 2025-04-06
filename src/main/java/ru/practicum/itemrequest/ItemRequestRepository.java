// ItemRequestRepository.java (Repository - in ru.practicum.request)
package ru.practicum.itemrequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir where ir.requester.id = :userId order by ir.created desc ")
    List<ItemRequest> findAllByRequesterId(Long userId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllPageable(Long userId, Pageable pageable);
}