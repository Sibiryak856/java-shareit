package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(long userId, Sort created);

    List<ItemRequest> findAllByRequestorIdNot(long userId, Pageable pageable);
}
