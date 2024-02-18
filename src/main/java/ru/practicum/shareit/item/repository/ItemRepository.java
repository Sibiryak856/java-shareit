package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(@Param("userId") Long userId);

    List<Item> findAllByNameOrDescriptionIgnoreCaseContainingAndAvailableEquals(
            String text1, String text2, Boolean available);

    void deleteByOwnerId(@Param("userId") Long userId);
}
