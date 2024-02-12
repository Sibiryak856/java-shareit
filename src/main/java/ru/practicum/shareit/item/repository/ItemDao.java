package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findByOwner(Long id);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCase(String text1, String text2);

    //@Query
    void deleteByOwner(Long id);
}
