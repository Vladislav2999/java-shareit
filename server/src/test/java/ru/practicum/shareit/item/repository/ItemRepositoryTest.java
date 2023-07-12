package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

    private static final int FIRST_PAGE = 0;

    private static final int SIZE_OF_10 = 10;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(new User(1L, "testName", "test@mail.com"));

        itemRepository
                .save(new Item(1L, "testName", "Description", true, user));

        itemRepository
                .save(new Item(2L, "secondName", "secondTestDescription", true, user));

        itemRepository
                .save(new Item(3L, "имя", "описание", true, user));

        itemRepository
                .save(new Item(4L, "fourthName", "secondTestDescription", false, user));
    }

    @Test
    void searchTest() {
        List<Item> search = itemRepository.search("tESt", PageRequest.of(FIRST_PAGE, SIZE_OF_10));

        Assertions.assertNotNull(search);
        Assertions.assertEquals(2, search.size());
    }

    @Test
    void searchTestWithNoItemsFound() {
        List<Item> results = itemRepository.search("noSuchSubstring", PageRequest.of(FIRST_PAGE, SIZE_OF_10));

        Assertions.assertNotNull(results);
        Assertions.assertEquals(0, results.size());
    }

}
