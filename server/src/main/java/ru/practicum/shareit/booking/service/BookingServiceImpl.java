package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(NewBookingDtoRequest dto) {
        User booker = userRepository.findById(dto.getBookerId())
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item Not Found"));
        if (!item.isAvailable()) {
            throw new IllegalArgumentException("Item is not available");
        }
        Booking saved = bookingRepository.save(bookingMapper.toNewBooking(dto, item, booker));
        return bookingMapper.toBookingDtoResponse(saved);
    }

    @Override
    public BookingDtoResponse approveBooking(long bookingId, long userId) {
        Booking saved = bookingRepository.save(changeBookingStatus(bookingId, userId, Status.APPROVED));
        return bookingMapper.toBookingDtoResponse(saved);
    }

    @Override
    public BookingDtoResponse rejectBooking(long bookingId, long userId) {
        Booking saved = bookingRepository.save(changeBookingStatus(bookingId, userId, Status.REJECTED));
        return bookingMapper.toBookingDtoResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingByIdOfBookerOrOwner(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllBookingsByBooker(long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        BooleanExpression expression = QBooking.booking.booker.id.eq(userId);
        BooleanExpression expressionByState = generatedQueryExpressionByState(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        Iterable<Booking> bookings = bookingRepository.findAll(expression.and(expressionByState), sort);
        return bookingMapper.toBookingDtoResponse((List<Booking>) bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getAllBookingsByOwner(long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        List<Item> allByOwnerId = itemRepository.findAllByOwnerId(userId);
        if (allByOwnerId.isEmpty()) {
            return new ArrayList<>();
        }
        BooleanExpression expression = QBooking.booking.item.owner.id.eq(userId);
        BooleanExpression expressionByState = generatedQueryExpressionByState(state);
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        Iterable<Booking> bookings = bookingRepository.findAll(expression.and(expressionByState), sort);
        return bookingMapper.toBookingDtoResponse((List<Booking>) bookings);
    }

    private Booking changeBookingStatus(long bookingId, long userId, Status status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to approve this booking");
        }
        booking.setStatus(status);
        return booking;
    }

    private BooleanExpression generatedQueryExpressionByState(BookingState state) {
        return switch (state) {
            case REJECTED -> QBooking.booking.status.eq(Status.REJECTED);
            case WAITING -> QBooking.booking.status.eq(Status.WAITING);
            case CURRENT -> QBooking.booking.startTime.before(LocalDateTime.now())
                    .and(QBooking.booking.endTime.after(LocalDateTime.now()))
                    .and(QBooking.booking.status.eq(Status.APPROVED));
            case PAST -> QBooking.booking.endTime.before(LocalDateTime.now())
                    .and(QBooking.booking.status.eq(Status.APPROVED));
            case FUTURE -> QBooking.booking.startTime.after(LocalDateTime.now());
            default -> null;
        };
    }
}
