package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest r " +
            "left join fetch r.items " +    // подгружаем items
            "where r.id = :id")
    Optional<ItemRequest> findByIdWithRelations(@Param("id") long id);

    @Query("select r from ItemRequest r left join fetch r.items where r.requester.id = :userId")
    List<ItemRequest> findAllByRequesterId(@Param("userId") long userId, Sort sort);

    @Query("select r from ItemRequest r left join fetch r.items where r.requester.id != :userId")
    List<ItemRequest> findAllOtherByRequesterId(@Param("userId") long userId, Sort sort);
}
