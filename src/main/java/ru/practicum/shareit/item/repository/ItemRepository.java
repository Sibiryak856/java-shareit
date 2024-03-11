package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT i FROM Item AS i " +
            "WHERE LOWER(i.name) LIKE %:text1% " +
            "OR LOWER(i.description) LIKE %:text2% " +
            "AND i.available = :available")
    List<Item> findAllAvailableBySearch(
            @Param("text1") String text1,
            @Param("text2") String text2,
            @Param("available") Boolean available,
            Pageable pageable);

    void deleteAllByOwnerId(Long userId);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);
}
