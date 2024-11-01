package ru.practicum.shareit.item.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("select i from Item i left join fetch i.comments c left join fetch c.author where i.owner.id = :userId")
    List<Item> findByOwnerId(@Param("userId") long ownerId, Sort sort);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    boolean existsByOwnerId(long userId);

    @Query("select i from Item i left join fetch i.comments c left join fetch c.author where i.id = :id")
    Optional<Item> findByIdWithRelations(@Param("id") long id);

    @Modifying
    @Query("delete from Item i where i.id = :id")
    int delete(@Param("id") long id);

    @Query("select i from Item i left join fetch i.comments c left join c.author where i.available = true "
            + "and (lower(i.name) like concat('%', lower(:text), '%') "
            + "or lower(i.description) like concat('%', lower(:text), '%'))")
    List<Item> findByNameOrDescription(@Param("text") String text, Sort sort);
}
