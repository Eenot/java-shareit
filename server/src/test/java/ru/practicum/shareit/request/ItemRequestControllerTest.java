package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validator.PageableValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Headers.USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    @MockBean
    PageableValidator pageableValidator;

    @SneakyThrows
    @Test
    void addNewRequest() {
        ItemRequestDto itemRequestToCreate = new ItemRequestDto();
        when(itemRequestService.createNewRequest(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestToCreate);

        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestToCreate))
                        .contentType("application/json")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestToCreate), result);
    }

    @SneakyThrows
    @Test
    void getAllUserItemsWithResponses() {
        mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllUserRequestsWithResponses(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllCreatedRequests() {
        doNothing().when(pageableValidator).checkingPageableParams(anyInt(), anyInt());

        mockMvc.perform(get("/requests/all")
                        .param("from", "1")
                        .param("size", "1")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getAllRequestsToResponse(anyLong(), any(Pageable.class));
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        long requestId = 0L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk());

        verify(itemRequestService, times(1)).getRequestById(anyLong(), anyLong());
    }
}