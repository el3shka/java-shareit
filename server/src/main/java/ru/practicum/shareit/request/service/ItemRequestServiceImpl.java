package ru.practicum.shareit.request.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j

public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository requestRepository;
    private final Validator validator;


    @Override
    public ItemRequest createItemRequest(ItemRequest request) {
        Objects.requireNonNull(request, "Cannot create item request: is null");
        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        Objects.requireNonNull(request.getRequester().getId(), "Cannot create item request: requester id is null");
        userService.getUser(request.getRequester().getId());

        final ItemRequest createdItemRequest = requestRepository.save(request);
        log.info(("Created item request with id = {}: {}"), createdItemRequest.getId(), createdItemRequest);
        return createdItemRequest;
    }

    @Override
    public ItemRequest getItemRequest(final long id) {
        return requestRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Request not found")
        );
    }

    @Override
    public ItemRequest getRequestById(long userId, long requestId) {
        log.info("Проверка наличия пользователя при поиске конкретного запроса с id = {} в БД", userId);
        userService.findUserById(userId);
        log.info("Пользователь userId={}", userId);
        log.info("Запрос requestId={}", requestId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Такой запрос с id = " + requestId + " не найден "));
    }


    @Override
    @Transactional(readOnly = true)
    public ItemRequest getItemRequestWithRelations(final long id, long userId) {
        userService.getUser(userId);
        ItemRequest itemRequest = requestRepository.findByIdWithRelations(id).orElseThrow(
                () -> new NotFoundException("Request not found")
        );
        log.info("Items in request: {}", itemRequest.getItems());
        return itemRequest;
    }

    @Override
    public List<ItemRequest> getOwnRequests(final long userId) {
        userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return requestRepository.findAllByRequesterId(userId, sort);
    }

    @Override
    public List<ItemRequest> getOthersRequests(long userId) {
        userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return requestRepository.findAllOtherByRequesterId(userId, sort);
    }
}
