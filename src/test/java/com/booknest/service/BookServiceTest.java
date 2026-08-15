package com.booknest.service;

import com.booknest.exception.BadRequestException;
import com.booknest.model.Book;
import com.booknest.repository.BookRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookById_ValidId() {
        String validId = "60c72b2f9b1d8b2d88a1b2c3"; // 24-character hex string
        Book mockBook = Book.builder().id(validId).title("Clean Code").build();
        when(bookRepository.findById(validId)).thenReturn(Optional.of(mockBook));

        Book result = bookService.getBookById(validId);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepository, times(1)).findById(validId);
    }

    @Test
    void testGetBookById_InvalidIdFormat() {
        String invalidId = "shortId";
        assertThrows(BadRequestException.class, () -> bookService.getBookById(invalidId));
        verify(bookRepository, never()).findById(anyString());
    }

    @Test
    void testCreateBook_SetsDefaults() {
        Book inputBook = Book.builder().title("Refactoring").type("PDF").pdfUrl("http://pdf").build();
        Book savedBook = Book.builder().id("60c72b2f9b1d8b2d88a1b2c4").title("Refactoring").status("Available").build();

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookService.createBook(inputBook, "owner123", "owner@example.com");

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
}
