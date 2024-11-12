package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository,
            new BookingMapper(), itemRepository, userRepository);


}