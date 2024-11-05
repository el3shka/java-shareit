package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @EntityGraph("request-with-items-owner")
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long requester);

    @EntityGraph("request-with-items-owner")
    Optional<ItemRequest> findById(long id);
}
