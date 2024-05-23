package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validator.PageableValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Headers.USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @MockBean
    PageableValidator pageableValidator;

    @SneakyThrows
    @Test
    void shouldCreateBookingTest() {
        BookingDto bookingToCreate = new BookingDto();
        when(bookingService.addBooking(any(BookingDto.class), anyLong())).thenReturn(bookingToCreate);

        String result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingToCreate))
                        .header(USER_ID, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingToCreate), result);
    }

    @SneakyThrows
    @Test
    void shouldApproveBookingTest() {
        BookingDto bookingToCreate = new BookingDto();
        bookingToCreate.setId(1L);
        BookingDto updatedBooking = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.approveBooking(anyLong(), anyLong(), anyString())).thenReturn(updatedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingToCreate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToCreate))
                        .header(USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedBooking), result);
    }

    @SneakyThrows
    @Test
    void shouldGetAllBookingsForUserTest() {
        mockMvc.perform(get("/bookings")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header(USER_ID, "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(1, 1);

        verify(bookingService, times(1)).getAllBookingsByUserId(1L, "ALL", PageRequest.of(1, 1));
    }

    @SneakyThrows
    @Test
    void shouldGetAllBookingsForOwnerTest() {
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header(USER_ID, "1"))
                .andExpect(status().isOk());
        doNothing().when(pageableValidator).checkingPageableParams(1, 1);

        verify(bookingService, times(1)).getAllBookingsByOwnerId(1L, "ALL", PageRequest.of(1, 1));
    }

    @SneakyThrows
    @Test
    void shouldGetInfoForBookingTest() {
        long bookingId = 0L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBookingInfo(anyLong(), anyLong());
    }
}
