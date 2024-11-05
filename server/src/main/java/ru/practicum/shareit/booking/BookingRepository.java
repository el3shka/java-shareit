package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query(value = """
            select b from Booking as b where b.id=?1 and (b.booker.id=?2 or b.item.owner.id=?2)
            """)
    Optional<Booking> findById(long bookingId, long userId);

    @Query(value = """
            select b from Booking as b where b.item.id in ?1
            and b.status='APPROVED'
            and b.startTime < ?2
            order by b.startTime desc
            limit 1
            """)
    Optional<Booking> findByItemLastBooking(Long itemId, LocalDateTime now);

    @Query(value = """
            select b from Booking as b where b.item.id in ?1
            and b.status='APPROVED'
            and b.startTime > ?2
            order by b.startTime asc
            limit 1
            """)
    Optional<Booking> findByItemNextBooking(Long itemId, LocalDateTime now);

    @Query(value = """
            select b from Booking as b
            join(select b.item.id as item, max(b.startTime) as firstStartTime
            from Booking as b
            where b.startTime < ?2 and b.status='APPROVED'
            group by b.item.id) as subquery on b.item.id=subquery.item and b.startTime = subquery.firstStartTime
            where b.item.id in ?1
            """)
    List<Booking> findAllByItemsLastBooking(List<Long> itemIds, LocalDateTime now);

    @Query(value = """
            select b from Booking as b
            join(select b.item.id as item, min(b.startTime) as firstStartTime
            from Booking as b
            where b.startTime > ?2 and b.status='APPROVED'
            group by b.item.id) as subquery on b.item.id=subquery.item and b.startTime = subquery.firstStartTime
            where b.item.id in ?1
            """)
    List<Booking> findAllByItemsNextBooking(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItemId(Long itemId);
}

