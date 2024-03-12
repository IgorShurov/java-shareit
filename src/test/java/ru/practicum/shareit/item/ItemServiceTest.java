package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemDto item1Dto;
    private ItemDto item2Dto;
    private ItemDto item1UpdateDto;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private Booking lastBooking;
    private Booking nextBooking;


    @BeforeEach
    public void setUp() {
        item1Dto = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        item2Dto = ItemDto.builder()
                .name("item2 test")
                .description("item2 test description")
                .available(Boolean.TRUE)
                .build();
        item1UpdateDto = ItemDto.builder()
                .name("updated name")
                .description("updated description")
                .available(Boolean.FALSE)
                .build();
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("item request1 description");
        itemRequest1.setRequester(user2);
        itemRequest1.setCreated(LocalDateTime.now());
    }

    @Test
    public void createAndGetItemById() {
        //given
        userRepository.save(user1);
        //when
        var savedItem = itemService.create(item1Dto, user1.getId());
        var findItem = itemService.getItemsByOwner(user1.getId(), savedItem.getId());
        //then
        assertThat(savedItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
    }

    @Test
    public void notExistingUserCreateItem() {
        assertThatThrownBy(
                //then
                () -> itemService.create(item1Dto, 1L)
        )
                //when
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void createItemWithItemRequest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        item1Dto.setRequestId(itemRequest1.getId());

        //when
        var savedItem = itemService.create(item1Dto, user1.getId());
        var findItem = itemService.getItemsByOwner(savedItem.getId(), user2.getId());
        var saveRequest = itemRequestRepository.findById(itemRequest1.getId()).get();
        //then
        assertThat(savedItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
        assertThat(saveRequest.equals(itemRequest1)).isFalse();
        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void updateItem() {
        //given
        userRepository.save(user1);
        //when
        var savedItem = itemService.create(item1Dto, user1.getId());
        var updatedItem = itemService.update(item1UpdateDto, savedItem.getId(), user1.getId());
        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
        //then
        assertThat(updatedItem.getName()).isEqualTo(item1UpdateDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(item1UpdateDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(item1UpdateDto.getAvailable());
    }

    @Test
    public void updateItemWithNotExistingItemId() {
        //given
        userRepository.save(user1);
        itemService.create(item1Dto, user1.getId());
        assertThatThrownBy(
                () -> itemService.update(item1UpdateDto, 2L, user1.getId())
        )
                //then
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    public void updateItemWithOtherUser() {
        //given
        userRepository.save(user1);
        //when
        var savedItem = itemService.create(item1Dto, user1.getId());
        assertThatThrownBy(
                () -> itemService.update(item1UpdateDto, savedItem.getId(), 2L)
        )
                //then
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getItemByNotExistingId() {
        //given
        userRepository.save(user1);
        //when
        itemService.create(item1Dto, user1.getId());
        assertThatThrownBy(
                () -> itemService.getItemsByOwner(2L, user1.getId())
                //then
        ).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getItemByIdWithLastAndNextBookings() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem = itemService.create(item1Dto, user1.getId());
        createLastAndNextBookings(savedItem);
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);
        //when
        var findItem = itemService.getItemsByOwner(savedItem.getId(), user1.getId());
        //then
        assertThat(findItem.getId()).isEqualTo(savedItem.getId());
        assertThat(findItem.getName()).isEqualTo(item1Dto.getName());
        assertThat(findItem.getDescription()).isEqualTo(item1Dto.getDescription());
        assertThat(findItem.getAvailable()).isEqualTo(item1Dto.getAvailable());
        assertThat(findItem.getLastBooking().getBookerId()).isEqualTo(user2.getId());
        assertThat(findItem.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(findItem.getNextBooking().getBookerId()).isEqualTo(user2.getId());
        assertThat(findItem.getNextBooking().getId()).isEqualTo(nextBooking.getId());
    }

    @Test
    public void getPersonalItems() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        itemService.create(item2Dto, user2.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);
        var findItem = itemService.getItemsByOwner(savedItem1.getId(), user1.getId());
        //when
        var personalItemsList = itemService.getItemsByOwner(user1.getId(), 0, 2);
        //then
        assertThat(personalItemsList).singleElement().usingRecursiveComparison()
                .ignoringFields("comments").isEqualTo(findItem);
    }

    @Test
    public void getPersonalItemsWithNotExistingUser() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        itemService.create(item2Dto, user2.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);
        assertThatThrownBy(
                //when
                () -> itemService.getItemsByOwner(99L, 0, 2)
                //then
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getFoundItems() {
        //given
        userRepository.save(user1);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        var savedItem2 = itemService.create(item2Dto, user1.getId());
        //when
        var findItems = itemService.search("em2", 0, 2);
        //then
        assertThat(findItems).singleElement().usingRecursiveComparison()
                .ignoringFields("comments").isEqualTo(savedItem2);
        //when
        findItems = itemService.search("test", 0, 2);
        //then
        assertThat(findItems.size()).isEqualTo(2);
        assertThat(findItems).element(0).usingRecursiveComparison()
                .ignoringFields("comments").isEqualTo(savedItem1);
        assertThat(findItems).element(1).usingRecursiveComparison()
                .ignoringFields("comments").isEqualTo(savedItem2);
    }

    @Test
    public void addComment() {
        //given
        CommentDto commentDto = CommentDto.builder()
                .text("test")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        //when
        var savedComment1 = itemService.addComment(user2.getId(), savedItem1.getId(), commentDto);
        var comment1 = commentRepository.findById(savedComment1.getId()).get();
        //then
        assertThat(savedComment1.getId()).isEqualTo(1L);
        assertThat(savedComment1.getText()).isEqualTo(commentDto.getText());
        assertThat(savedComment1.getCreated()).isBefore(LocalDateTime.now());
        assertThat(savedComment1.getAuthorName()).isEqualTo(user2.getName());
        //when
        commentDto.setText("test1");
        var savedComment2 = itemService.addComment(user2.getId(), savedItem1.getId(), commentDto);
        var comment2 = commentRepository.findById(savedComment2.getId()).get();
        //then
        assertThat(comment1.equals(comment2)).isFalse();

    }

    @Test
    public void addCommentFromUserWithNotExistingBooks() {
        //given
        CommentDto commentDto = CommentDto.builder()
                .text("test")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemService.addComment(user2.getId(), savedItem1.getId(), commentDto)
                //then
        ).isInstanceOf(CommentValidationException.class);
    }

    @Test
    public void addCommentForNotExistingItem() {
        //given
        CommentDto commentDto = CommentDto.builder()
                .text("test")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        assertThat(lastBooking.equals(nextBooking)).isFalse();
        assertThatThrownBy(
                //when
                () -> itemService.addComment(2L, user2.getId(), commentDto)
                //then
        ).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    public void addCommentFromNotExistingUser() {
        //given
        CommentDto commentDto = CommentDto.builder()
                .text("test")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.create(item1Dto, user1.getId());
        createLastAndNextBookings(savedItem1);
        bookingRepository.save(lastBooking);
        assertThatThrownBy(
                //when
                () -> itemService.addComment(99L, savedItem1.getId(), commentDto)
                //then
        ).isInstanceOf(UserNotFoundException.class);
    }

    private void createLastAndNextBookings(ItemDto item) {
        Item bookingItem = new Item();
        bookingItem.setId(item.getId());
        bookingItem.setUser(user1);
        bookingItem.setName(item.getName());
        bookingItem.setDescription(item.getDescription());
        bookingItem.setAvailable(item.getAvailable());
        lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(bookingItem);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(Status.APPROVED);
        nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(bookingItem);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(Status.APPROVED);
    }
}