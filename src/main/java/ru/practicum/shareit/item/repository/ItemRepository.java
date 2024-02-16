package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /*@Query("SELECT I FROM ITEMS AS I " +
            "WHERE I.USER.ID = :userId")*/
    List<Item> findAllByOwnerId(@Param("userId") Long userId);

    /*@Query("SELECT I FROM ITEMS AS I " +
            "WHERE I.AVAILABLE = true " +
            "AND (I.NAME ILIKE %:text% " +
            "OR I.DESCRIPTION ILIKE %:text%)")*/
    List<Item> findAllByNameOrDescriptionIgnoreCaseContainingAndAvailableEquals(
            String text1, String text2, Boolean available);

    /*@Query("DELETE FROM ITEMS AS I " +
            "WHERE I.USER.ID = :userId")*/
    void deleteByOwnerId(@Param("userId") Long userId);
}
