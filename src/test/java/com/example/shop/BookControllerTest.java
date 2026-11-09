package com.example.shop;

import com.example.shop.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @LocalServerPort
    private int port;

    private WebTestClient client() {
        return WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    public void testGetAllBooks() {
        List<Book> books = client().get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    public void testGetBookById() {
        Book book = client().get()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(book);
        assertEquals(1L, book.getId());
    }

    @Test
    public void testGetBookByIdNotFound() {
        client().get()
                .uri("/api/books/99999")
                .exchange()
                .expectStatus().isNotFound();
    }
}