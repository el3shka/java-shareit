package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(itemRequestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        ItemRequest saved = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, user));
        return itemRequestMapper.toItemRequestDto(saved);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<ItemRequestWithItemInfoDto> getItemRequestsByUser(long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return itemRequestMapper.toItemRequestWithItemInfoDto(requests);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(int from, int size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findAll(page).getContent();
        return itemRequestMapper.toItemRequestDto(requests);
    }

    @Override
    public ItemRequestWithItemInfoDto getItemRequestById(long id) {
        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item Not Found"));
        return itemRequestMapper.toItemRequestWithItemInfoDto(request);
    }
}
