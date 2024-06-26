package ru.practicum.shareit.item.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.UnsupportedMethodException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.inMemory.ItemRepositoryInMemoryImpl;
import ru.practicum.shareit.item.service.ItemServiceInMemoryImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ItemServiceInMemoryImplTest {

    private Item expectedItem;

    @Mock
    ItemRepositoryInMemoryImpl itemRepositoryInMemory;

    @InjectMocks
    ItemServiceInMemoryImpl itemServiceInMemory;


    @BeforeEach
    public void fillData() {
        expectedItem = Item.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name")
                .owner(User.builder().id(1L).build())
                .build();
    }

    @Test
    void create_whenAllDataIsCorrect_thenReturnItem() {
        when(itemRepositoryInMemory.createItem(expectedItem, 1L)).thenReturn(expectedItem);

        ItemDto actual = itemServiceInMemory.createItem(toItemDto(expectedItem), 1L);

        assertEquals(actual.getAvailable(), expectedItem.getAvailable());
    }

    @Test
    void create_whenDataIsIncorrect_thenThrowEmptyFieldExceptionException() {
        EmptyFieldException emptyFieldException = assertThrows(EmptyFieldException.class,
                () -> itemServiceInMemory.createItem(toItemDto(new Item()), 1L), "Исключения различаются!");

        assertEquals(emptyFieldException.getMessage(), "Поля не могут быть пусты!", "Сообщения различаются!");
    }

    @Test
    void update_whenAllDataCorrect_thenReturnItem() {
        when(itemRepositoryInMemory.updateItem(expectedItem, 1L)).thenReturn(expectedItem);
        when(itemRepositoryInMemory.getItemById(anyLong())).thenReturn(expectedItem);

        ItemDto actualItem = itemServiceInMemory.updateItem(toItemDto(expectedItem), 1L);

        assertEquals(expectedItem.getName(), actualItem.getName());
    }

    @Test
    void getItemById_whenItemExists_thenReturnItem() {
        when(itemRepositoryInMemory.getItemById(anyLong())).thenReturn(expectedItem);

        ItemDto actual = itemServiceInMemory.getItemById(1L, 1L);

        assertEquals(actual.getId(), expectedItem.getId());
    }

    @Test
    void getItemsByUserId_whenUserExist_thenReturnListIfExpectedItem() {
        when(itemRepositoryInMemory.getItemsByUserId(anyLong())).thenReturn(List.of(expectedItem));

        List<ItemDto> actualList = new ArrayList<>(itemServiceInMemory
                .getItemsByUserId(1L, PageRequest.of(1, 10)));

        assertEquals(actualList.size(), 1);
    }

    @Test
    void getItemsBySearch_whenTextIsEmpty_thenReturnEmptyString() {
        List<ItemDto> items = new ArrayList<>(itemServiceInMemory.getItemsBySearching("", PageRequest.of(1, 10)));

        assertEquals(items.size(), 0);
    }

    @Test
    void getItemsBySearch_whenTextIsNotEmpty_thenReturnListOfExpectedItem() {
        when(itemRepositoryInMemory.getItemsBySearching("desc")).thenReturn(List.of(expectedItem));
        List<ItemDto> items = new ArrayList<>(itemServiceInMemory.getItemsBySearching("desc", PageRequest.of(1, 10)));

        assertEquals(items.size(), 1);
    }


    @Test
    void checkItemOwner() {
        UnsupportedMethodException exception = assertThrows(UnsupportedMethodException.class,
                () -> itemServiceInMemory.checkItemOwner(1L, 1L));

        assertEquals(exception.getMessage(), "inMemory checkItemOwner");
    }

    @Test
    void addCommentToItem() {
        UnsupportedMethodException exception = assertThrows(UnsupportedMethodException.class,
                () -> itemServiceInMemory.addCommentToItem(1L, 1L, new CommentDto()));

        assertEquals(exception.getMessage(), "inMemory addCommentToItem");
    }
}
