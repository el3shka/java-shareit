package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id  - " + userId + " not found"));
        ItemRequest itemRequest = null;
        if (!(itemDto.getRequestId() == null)) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request with id - " + itemDto.getRequestId() + " not found"));
        }
        Item item = itemRepository.save(itemMapper.fromDto(itemDto, user, itemRequest));
        return itemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId) {
        checkIfExistsUser(userId);
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new NotFoundException("Item with id - " + itemDto.getId() + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException("The user with the id  - " + userId + " is not the owner");
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item update = itemRepository.save(item);
        return itemMapper.toDto(update);
    }

    @Override
    public ItemInfoDto getById(Long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id - " + id + " not found"));
        List<Comment> commentsByItem = commentRepository.findAllByItemId(item.getId());
        if (!item.getOwner().getId().equals(userId)) {
            return itemMapper.toInfoDto(item, null, null, commentsByItem);
        }
        Booking nextBooking = bookingRepository.findByItemNextBooking(item.getId(), LocalDateTime.now())
                .orElse(null);
        Booking lastBooking = bookingRepository.findByItemLastBooking(item.getId(), LocalDateTime.now())
                .orElse(null);
        return itemMapper.toInfoDto(item, lastBooking, nextBooking, commentsByItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemInfoDto> getAllOfOwner(Long userId) {
        checkIfExistsUser(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, item -> item));
        List<Booking> lastBookings = bookingRepository
                .findAllByItemsLastBooking(new ArrayList<>(itemMap.keySet()), LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository
                .findAllByItemsNextBooking(new ArrayList<>(itemMap.keySet()), LocalDateTime.now());
        Map<Long, Booking> next = nextBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, Booking> last = lastBookings.stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        List<Comment> comments = commentRepository.findAllByItemIdIn(new ArrayList<>(itemMap.keySet()));
        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        return itemMap.keySet().stream()
                .map(id -> itemMapper.toInfoDto(itemMap.get(id),
                        last.get(id),
                        next.get(id),
                        commentsByItem.get(id)))
                .toList();
    }

    @Override
    public List<ItemDto> findByNameByDescription(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllByNameOrDescription(text);
        return itemMapper.toDtoList(items);
    }

    @Override
    public CommentDtoResponse addComment(CommentDtoRequest commentDtoRequest) {
        Item item = itemRepository.findById(commentDtoRequest.getItemId()).orElseThrow(
                () -> new NotFoundException("Item with id - " + commentDtoRequest.getItemId() + " not found"));
        User user = userRepository.findById(commentDtoRequest.getUserId()).orElseThrow(
                () -> new NotFoundException("User with id - " + commentDtoRequest.getUserId() + " not found"));
        boolean isRentUserItem = bookingRepository.findAllByItemId(item.getId()).stream()
                .anyMatch(booking -> booking.getBooker().equals(user)
                        && booking.getEndTime().isBefore(LocalDateTime.now()));
        if (!isRentUserItem) {
            throw new IllegalArgumentException("The user with the id - " + user.getId() + " is not rent this item");
        }
        Comment savedComment = commentRepository.save(commentMapper.toComment(commentDtoRequest, item, user));
        return commentMapper.toCommentDtoResponse(savedComment);
    }

    private void checkIfExistsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id - " + userId + " not found");
        }
    }
}
