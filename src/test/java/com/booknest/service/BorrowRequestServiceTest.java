package com.booknest.service;

import com.booknest.exception.BadRequestException;
import com.booknest.model.Book;
import com.booknest.model.BorrowRequest;
import com.booknest.repository.BookRepository;
import com.booknest.repository.BorrowRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowRequestServiceTest {

    @Mock
    private BorrowRequestRepository borrowRequestRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BorrowRequestService borrowRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // default mock for valid ID check
        when(bookService.isValidObjectId(anyString())).thenReturn(true);
    }

    @Test
    void testCreateBorrowRequest_OwnBook() {
        String bookId = "60c72b2f9b1d8b2d88a1b2c3";
        String userEmail = "owner@example.com";
        Book mockBook = Book.builder().id(bookId).ownerEmail(userEmail).title("Clean Code").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        assertThrows(BadRequestException.class, () -> 
                borrowRequestService.createBorrowRequest(bookId, "BorrowerName", userEmail)
        );

        verify(borrowRequestRepository, never()).save(any(BorrowRequest.class));
    }

    @Test
    void testCreateBorrowRequest_DuplicatePending() {
        String bookId = "60c72b2f9b1d8b2d88a1b2c3";
        String ownerEmail = "owner@example.com";
        String borrowerEmail = "borrower@example.com";
        Book mockBook = Book.builder().id(bookId).ownerEmail(ownerEmail).title("Clean Code").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(borrowRequestRepository.findByBookIdAndBorrowerEmailAndStatus(bookId, borrowerEmail, "pending"))
                .thenReturn(Optional.of(new BorrowRequest()));

        assertThrows(BadRequestException.class, () -> 
                borrowRequestService.createBorrowRequest(bookId, "BorrowerName", borrowerEmail)
        );

        verify(borrowRequestRepository, never()).save(any(BorrowRequest.class));
    }
}
