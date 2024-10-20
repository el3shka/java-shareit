package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Optional<Item> itemById = itemRepository.findById(bookingDto.getItemId());
        if (itemById.isEmpty()) {
            throw new NotFoundException("Вещь не найдена.");
        }
        Item item = itemById.get();
        bookingValidation(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBookingDetails(userId, bookingId, 1);
        assert booking != null;
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = validateBookingDetails(userId, bookingId, 2);
        assert booking != null;
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAll(Long bookerId, String state) {
        userService.findById(bookerId);
        return switch (validState(state)) {
            case ALL -> bookingRepository.findAllBookingsByBookerId(bookerId).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case CURRENT -> bookingRepository.findAllCurrentBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case PAST -> bookingRepository.findAllPastBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case FUTURE -> bookingRepository.findAllFutureBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case WAITING -> bookingRepository.findAllWaitingBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case REJECTED -> bookingRepository.findAllRejectedBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        };
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllOwner(Long ownerId, String state) {
        userService.findById(ownerId);
        return switch (validState(state)) {
            case ALL -> bookingRepository.findAllBookingsByOwnerId(ownerId).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case CURRENT -> bookingRepository.findAllCurrentBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case PAST -> bookingRepository.findAllPastBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case FUTURE -> bookingRepository.findAllFutureBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case WAITING -> bookingRepository.findAllWaitingBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            case REJECTED -> bookingRepository.findAllRejectedBookingsByOwnerId(ownerId).stream()
                    .map(BookingMapper::toBookingOut)
                    .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        };
    }


    public void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступена для бронирования.");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вещь не найдена.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }
    }

    public BookingState validState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
        return state;
    }

    public Booking validateBookingDetails(Long userId, Long bookingId, Integer number) {
        Optional<Booking> bookingById = bookingRepository.findById(bookingId);
        if (bookingById.isEmpty()) {
            throw new NotFoundException("Бронь не найдена.");
        }
        Booking booking = bookingById.get();
        switch (number) {
            case 1:
                if (!booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("Пользователь не является владельцем");
                }
                if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                    throw new ValidationException("Бронь cо статусом WAITING");
                }
                return booking;
            case 2:
                if (!booking.getBooker().getId().equals(userId)
                        && !booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("Пользователь не владелeц и не автор бронирования ");
                }
                return booking;
        }
        return null;
    }
}