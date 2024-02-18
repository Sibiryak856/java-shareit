package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /*@Query("SELECT C FROM COMMENTS AS C " +
            "WHERE C.ITEM_ID = :itemId")*/
    List<Comment> findAllByItemIdOrderByCreatedDesc(@Param("itemId") Long itemId);

    /*@Query("SELECT C FROM COMMENTS AS C " +
            "WHERE C.ITEM_ID IN (itemsId)")*/
    List<Comment> findAllByItemIdIn(@Param("itemsId") List<Long> itemsId);


    /**
     * TODO add delete
     */


}
