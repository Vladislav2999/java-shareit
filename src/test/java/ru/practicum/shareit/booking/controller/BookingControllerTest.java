package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);

    private static final LocalDateTime END = LocalDateTime.now().plusDays(2);

    @MockBean
    private static BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private BookingDtoIn bookingDtoIn;

    private BookingDtoOut bookingDtoOut;

    @BeforeEach
    void beforeEach() {
        bookingDtoIn = new BookingDtoIn(1L, START, END, 1L);

        bookingDtoOut = new BookingDtoOut(1L,
                START,
                END,
                new BookingDtoOut.Item(1L, "name"),
                new BookingDtoOut.Booker(1L, "name"),
                Status.WAITING);
    }

    @Test
    void createTest() throws Exception {
        when(bookingService.create(any(BookingDtoIn.class), anyLong()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void createTestStartIsAfterEnd() throws Exception {
        bookingDtoIn.setStart(LocalDateTime.now().plusDays(5));
        bookingDtoIn.setEnd(LocalDateTime.now().plusDays(2));
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTest() throws Exception {
        bookingDtoOut.setStatus(Status.APPROVED);
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));

    }

    @Test
    void getByOwnerTest() throws Exception {
        when(bookingService.getByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoOut))));
    }

    @Test
    void getByBookerTest() throws Exception {
        when(bookingService.getByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoOut))));
    }


}
