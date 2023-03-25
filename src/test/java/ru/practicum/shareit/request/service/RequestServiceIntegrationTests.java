package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTests {

    private final RequestService requestService;

    private final UserService userService;

    private ItemRequestDto requestDto;

    private User owner;

    private User requestor;

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "owner", "owner@mail.ru");
        requestor = new User(2L, "requestor", "requestor@mail.ru");
        requestDto = new ItemRequestDto(null, "description", null, null);
    }


    @Test
    void createTest() {
        User savedRequestor = userService.create(requestor);
        ItemRequestDto savedRequest = requestService.create(savedRequestor.getId(), requestDto);
        ItemRequestDto foundRequest = requestService.findByRequestId(savedRequestor.getId(), savedRequest.getId());

        assertNotNull(savedRequest);
        assertEquals(savedRequest.getId(), foundRequest.getId());
        assertEquals(savedRequest.getDescription(), foundRequest.getDescription());
    }

    @Test
    void findAllRequestsTest() {
        User savedUser = userService.create(owner);
        User savedRequestor = userService.create(requestor);
        ItemRequestDto savedRequest = requestService.create(savedRequestor.getId(), requestDto);

        List<ItemRequestDto> foundRequests = requestService.findAllRequests(savedUser.getId(), 0, 99);

        assertNotNull(foundRequests);
        assertEquals(1, foundRequests.size());
        assertEquals(savedRequest.getDescription(), foundRequests.get(0).getDescription());
    }

    @Test
    void findAllByUserIdTest() {
        userService.create(owner);
        User savedRequestor = userService.create(requestor);
        ItemRequestDto savedRequest = requestService.create(savedRequestor.getId(), requestDto);

        List<ItemRequestDto> foundRequests = requestService.findAllByUserId(savedRequestor.getId());

        assertNotNull(foundRequests);
        assertEquals(1, foundRequests.size());
        assertEquals(savedRequest.getDescription(), foundRequests.get(0).getDescription());
    }

    @Test
    void findByRequestIdTest() {
        User savedRequestor = userService.create(requestor);
        ItemRequestDto savedRequest = requestService.create(savedRequestor.getId(), requestDto);

        ItemRequestDto foundRequest = requestService.findByRequestId(savedRequestor.getId(), savedRequest.getId());

        assertNotNull(foundRequest);
        assertEquals(savedRequest.getDescription(), foundRequest.getDescription());
    }
}
