package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.ItemRequestValidator;
import ru.practicum.shareit.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.dto.mapper.ItemRequestMapper.toItemRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserValidator userValidator;

    @Mock
    ItemRequestValidator itemRequestValidator;

    @InjectMocks
    ItemRequestServiceImpl requestService;

    @Test
    void addNewRequest_whenUserExists_thenReturnItemRequestDto() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(new User())
                .description("text")
                .creationDate(LocalDateTime.now())
                .build();
        itemRequest.setResponsesToRequest(List.of(Item.builder().request(itemRequest).build()));
        when(userValidator.validateUserIdAndReturnIt(anyLong())).thenReturn(new User());
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualRequest = requestService.createNewRequest(toItemRequestDto(itemRequest), 1L);

        assertEquals(actualRequest.getDescription(), itemRequest.getDescription());
        verify(itemRequestValidator, times(1)).validateRequestData(any(ItemRequestDto.class));
    }

    @Test
    void addNewRequest_whenUserNotExists_thenThrowEntityNotFoundException() {
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : " + 1L));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.createNewRequest(toItemRequestDto(new ItemRequest()), 1L), "Исключения различаются");

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 1", "сообщения различаются");
    }

    @Test
    void addNewRequest_whenUserExistsAndRequestDataIsIncorrect_thenThrowIncorrectDataException() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(new User())
                .description("text")
                .creationDate(LocalDateTime.now())
                .build();
        itemRequest.setResponsesToRequest(List.of(Item.builder().request(itemRequest).build()));
        when(userValidator.validateUserIdAndReturnIt(anyLong()))
                .thenThrow(new IncorrectDataException("Описание запроса не может быть пустым!"));

        IncorrectDataException incorrectDataException = assertThrows(IncorrectDataException.class,
                () -> requestService.createNewRequest(toItemRequestDto(itemRequest), 1L));

        assertEquals(incorrectDataException.getMessage(), "Описание запроса не может быть пустым!", "Сообщения различаются");
    }

    @Test
    void getAllUserRequestsWithResponses_whenUserExists_thenReturnListOfResponses() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(itemRequestRepository.findAllByRequester_Id(anyLong())).thenReturn(List.of(new ItemRequest()));

        List<ItemRequestDto> requests = new ArrayList<>(requestService.getAllUserRequestsWithResponses(1L));

        assertEquals(requests.size(), 1);
    }

    @Test
    void getAllUserRequestsWithResponses_whenUserNotExists_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : 1"))
                .when(userValidator).validateUserId(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.getAllUserRequestsWithResponses(1L));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 1");
    }

    @Test
    void getAllRequestsToResponse_whenUserExists_thenReturnListOfRequests() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(itemRequestRepository.findAllByAllOtherUsers(anyLong(), any(Pageable.class))).thenReturn(List.of(new ItemRequest()));

        List<ItemRequestDto> requests = new ArrayList<>(requestService.getAllRequestsToResponse(1L, PageRequest.of(0, 10)));

        assertEquals(requests.size(), 1);
    }

    @Test
    void getAllRequestsToResponse_whenUserNotExists_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : 1"))
                .when(userValidator).validateUserId(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.getAllRequestsToResponse(1L, PageRequest.of(0, 10)));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 1");
    }

    @Test
    void getRequestById_whenUserAndRequestExist_thenReturnRequest() {
        ItemRequest expectedRequest = new ItemRequest();
        doNothing().when(userValidator).validateUserId(anyLong());
        when(itemRequestValidator.validateItemRequestIdAndReturnIt(anyLong())).thenReturn(new ItemRequest());

        ItemRequestDto actualRequest = requestService.getRequestById(1L, 1L);

        assertEquals(actualRequest, toItemRequestDto(expectedRequest));
    }

    @Test
    void getRequestById_whenUserNotFound_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : 1"))
                .when(userValidator).validateUserId(anyLong());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequestById(1L, 1L));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 1");
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowEntityNotFoundException() {
        doNothing().when(userValidator).validateUserId(anyLong());
        when(itemRequestValidator.validateItemRequestIdAndReturnIt(anyLong()))
                .thenThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : 1"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequestById(1L, 1L));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 1");
    }
}